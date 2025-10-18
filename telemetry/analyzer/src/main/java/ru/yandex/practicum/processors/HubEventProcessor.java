package ru.yandex.practicum.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handlers.HubEventHandler;
import ru.yandex.practicum.handlers.HubHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> kafkaConsumer;
    private final HubHandler hubHandler;

    @Value("${topics.hub-event-topic}")
    private String topic;

    @Override
    public void run() {
        try {
            kafkaConsumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(kafkaConsumer::wakeup));
            Map<String, HubEventHandler> mapBuilder = hubHandler.getHandlers();

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = kafkaConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String payloadName = event.getPayload().getClass().getSimpleName();
                    if (mapBuilder.containsKey(payloadName)) {
                        mapBuilder.get(payloadName).handle(event);
                    } else {
                        throw new IllegalArgumentException("Нет обработчика для события " + event);
                    }
                }
                kafkaConsumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка получения данных {}", topic);
        } finally {
            try {
                kafkaConsumer.commitSync();
            } finally {
                kafkaConsumer.close();
            }
        }
    }
}
