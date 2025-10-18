package ru.yandex.practicum.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handlers.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final KafkaConsumer<String, SensorsSnapshotAvro> kafkaConsumer;
    private final SnapshotHandler snapshotHandler;

    @Value("${topics.snapshots-topic}")
    private String snapshotTopic;

    public void start() {
        try {
            kafkaConsumer.subscribe(List.of(snapshotTopic));

            Runtime.getRuntime().addShutdownHook(new Thread(kafkaConsumer::wakeup));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = kafkaConsumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    log.info("Got snapshot for handling {}", snapshot);
                    snapshotHandler.handleSnapshot(snapshot);
                }
                kafkaConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                kafkaConsumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                kafkaConsumer.close();
            }
        }
    }
}
