package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.snapshot.SnapshotHandler;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class SnapshotProcessor implements Runnable {

    private final Consumer<Void, SensorsSnapshotAvro> consumer;
    private final SnapshotHandler snapshotHandler;
    private final KafkaConfig kafkaConfig;
    private String snaphots = "snapshots";

    public SnapshotProcessor(SnapshotHandler snapshotHandler,
                             KafkaConfig kafkaConfig) {
        this.consumer = new KafkaConsumer<>(kafkaConfig.getConsumerSnapshotProperties());
        this.snapshotHandler = snapshotHandler;
        this.kafkaConfig = kafkaConfig;
    }

    public void run() {
        try {
            consumer.subscribe(List.of(kafkaConfig.getTopics().get(snaphots)));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<Void, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<Void, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro sensorsSnapshot = record.value();
                    log.info("Получен снимок умного дома: {}", sensorsSnapshot);
                    snapshotHandler.buildSnapshot(sensorsSnapshot);
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка получения данных {}", kafkaConfig.getTopics().get(snaphots));
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
