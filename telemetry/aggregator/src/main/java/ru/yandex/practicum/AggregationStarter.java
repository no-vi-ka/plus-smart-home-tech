package ru.yandex.practicum;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.AggregatorService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private static final String SENSOR_SNAPSHOT_TOPIC = "telemetry.snapshots.v1";

    private final Consumer<String, SpecificRecordBase> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final AggregatorService aggregatorService;

    @PostConstruct
    public void start() {
        consumer.subscribe(List.of(SENSOR_TOPIC));

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(1));
                    if (!records.isEmpty()) {
                        try {
                            for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                                aggregatorService.aggregate((SensorEventAvro) record.value()).ifPresent(snapshot -> {
                                    producer.send(new ProducerRecord<>(
                                            SENSOR_SNAPSHOT_TOPIC,
                                            null,
                                            snapshot.getTimestamp().toEpochMilli(),
                                            snapshot.getHubId(),
                                            snapshot
                                    ));
                                });

                            }
                            consumer.commitSync();

                        } catch (Exception e) {
                            log.error("Ошибка при обработке батча сообщений, оффсеты не зафиксированы", e);
                        }
                    }}
            } catch (WakeupException ignored) {
                log.info("Получен сигнал остановки консьюмера");
            } catch (Exception e) {
                log.error("Ошибка во время обработки событий от датчиков", e);
            } finally {
                try {
                    log.info("Флашим продюсер перед закрытием");
                    producer.flush();
                } finally {
                    log.info("Закрываем консьюмер");
                    consumer.close(Duration.ofSeconds(10));
                    log.info("Закрываем продюсер");
                    producer.close(Duration.ofSeconds(10));
                }
            }
        }, "aggregation-thread");

        thread.start();
    }
}
