package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventHandler;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {

    private final Consumer<Void, HubEventAvro> consumer;
    private final Map<String, HubEventHandler> handlers;
    private final KafkaConfig kafkaConfig;
    private String hubs = "hubs";

    public HubEventProcessor(Set<HubEventHandler> handlers,
                             KafkaConfig kafkaConfig) {
        this.consumer = new KafkaConsumer<>(kafkaConfig.getConsumerHubProperties());
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(kafkaConfig.getTopics().get(hubs)));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<Void, HubEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<Void, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String payloadName = event.getPayload().getClass().getSimpleName();
                    log.info("Получение хаба {}", payloadName);
                    if (handlers.containsKey(payloadName)) {
                        handlers.get(payloadName).handle(event);
                    } else {
                        throw new IllegalArgumentException(String.format("Нет обработчика для события: %s", event));
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Ошибка получения данных {}", hubs);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
