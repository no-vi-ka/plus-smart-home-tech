package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class AggregationStarter {
    @Value("${kafka.attempt-timeout}")
    int attemptTimeout;
    @Value("${topic.telemetry.sensors}")
    String topicTelemetrySensors;
    @Value("${topic.telemetry.snapshots}")
    String topicTelemetrySnapshots;

    private final KafkaConsumer<String, SensorEventAvro> kafkaConsumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> kafkaProducer;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public AggregationStarter(KafkaConsumer<String, SensorEventAvro> kafkaConsumer, KafkaProducer<String, SensorsSnapshotAvro> kafkaProducer) {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaProducer = kafkaProducer;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { kafkaConsumer.wakeup(); }));
    }

    public void start() {
        try {
            kafkaConsumer.subscribe(List.of(topicTelemetrySensors));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = kafkaConsumer.poll(Duration.ofMillis(attemptTimeout));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    log.info("== Polling cycle iteration on records. Partition = {}, Offset = {}", record.partition(), record.offset());
                    SensorEventAvro event = record.value();
                    Optional<SensorsSnapshotAvro> sensorsSnapshotAvro = updateState(event);

                    if (sensorsSnapshotAvro.isPresent()) {
                        SensorsSnapshotAvro snapshotAvro = sensorsSnapshotAvro.get();
                        ProducerRecord<String, SensorsSnapshotAvro> producerRecord =
                                new ProducerRecord<>(topicTelemetrySnapshots,
                                        null,
                                        snapshotAvro.getTimestamp().toEpochMilli(),
                                        snapshotAvro.getHubId(),
                                        snapshotAvro);
                        kafkaProducer.send(producerRecord);
                        log.info("<== Has been sent on topic {} partition {} - the snapshotAvro {}", topicTelemetrySnapshots, producerRecord.partition(), snapshotAvro);
                    }
                }
                kafkaConsumer.commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.warn("Sensor event processor have got an error", e);
        } finally {
            try {
                kafkaProducer.flush();
                kafkaConsumer.commitSync();
            } finally {
                log.info("Aggregator consumer is closing");
                kafkaConsumer.close();
                log.info("Aggregator producer is closing");
                kafkaProducer.close();
            }
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("==> Update state for event: {}", event);

        SensorsSnapshotAvro snapshotAvro;
        String hubId = event.getHubId();
        String eventId = event.getId();

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, s -> {
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setHubId(hubId);
            newSnapshot.setTimestamp(event.getTimestamp());
            newSnapshot.setSensorsState(new HashMap<>());
            return newSnapshot;
        });

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        SensorStateAvro oldState = sensorsState.get(eventId);
        if (oldState != null && (oldState.getTimestamp().isAfter(event.getTimestamp())
                || oldState.getData().equals(event.getPayload()))) {
            return Optional.empty();
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());
        sensorsState.put(eventId, newState);
        snapshot.setTimestamp(event.getTimestamp());

        log.info("<== Updated snapshot: {}", snapshot);
        return Optional.of(snapshot);
    }
}
