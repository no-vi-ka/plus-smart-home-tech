package ru.yandex.practicum.analyzer.service.hub;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.exception.NotFoundException;
import ru.yandex.practicum.analyzer.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

/**
 * Запускает цикл опроса и обработки событий добавления/удаления устройств и сценариев
 * Поток сообщений о добавлении и удалении устройств и сценариев будет низкоинтенсивным.
 * Повторная обработка таких сообщений не критична.
 */
@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final HubService hubService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets;
    private final KafkaConfig.ConsumerHubConfig consumerConfig;
    private final Consumer<String, HubEventAvro> consumer;
    private volatile boolean running = true;

    public HubEventProcessor(HubService hubService, KafkaConfig kafkaConfig) {
        this.hubService = hubService;
        this.consumerConfig = kafkaConfig.getConsumerHubConfig();
        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.currentOffsets = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(consumerConfig.getTopics());

            // Цикл обработки событий
            while (running) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(consumerConfig.getPoolTimeout());
                int count = 0;
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    // обрабатываем очередную запись
                    handleRecord(record);
                    // фиксируем оффсеты обработанных записей, если нужно
                    manageOffsets(record, count);
                    count++;
                }
                // at-least-once для наибольшего сообщения, асинхронно, синхронная фиксация в блоке finally
                consumer.commitAsync();
            }
            log.info("Выполнение цикла было остановлено вручную");
        } catch (WakeupException e) {
            // лоигрование и закрытие консьюмера и продюсера в блоке finally
            log.warn("Возник WakeupException, running={}, msg={}, stackTrace={}",
                    running,
                    e.getMessage(),
                    Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                // фиксируем синхронно последний обработанный оффсет для гарантий at-least-once
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.wakeup();
        running = false;
    }

    private void manageOffsets(ConsumerRecord<String, HubEventAvro> record, int count) {
        // обновляем текущий оффсет для топика-партиции
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1));

        if (count % 10 == 0) {
            log.debug("count={}", count);
            OptionalLong maxOptional = currentOffsets.values().stream()
                    .mapToLong(OffsetAndMetadata::offset)
                    .max();
            maxOptional.ifPresent(max -> log.debug("Фиксация оффсетов max={}", max));

            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception == null) {
                    log.debug("Успешная фиксация оффсетов: {}", offsets);
                } else {
                    log.error("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, HubEventAvro> record) {
        log.info("топик = {}, партиция = {}, смещение = {}, значение: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        final String hubId = record.value().getHubId();
        final Object payload = record.value().getPayload();

        switch (payload) {
            case DeviceAddedEventAvro deviceAdded -> hubService.addDevice(deviceAdded, hubId);
            case DeviceRemovedEventAvro deviceRemoved -> hubService.removeDevice(deviceRemoved);
            case ScenarioAddedEventAvro scenarioAdded -> hubService.addScenario(scenarioAdded, hubId);
            case ScenarioRemovedEventAvro scenarioRemoved -> hubService.removeScenario(scenarioRemoved, hubId);
            default -> {
                String msg = "Неизвестный payload=" + payload.getClass();
                log.error(msg);
                throw new NotFoundException(msg);
            }
        }

    }
}
