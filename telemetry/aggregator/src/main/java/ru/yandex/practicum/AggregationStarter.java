package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafkaConfig.KafkaConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();
    private final EnumMap<KafkaConfig.TopicType, String> topics;
    private final KafkaConfig kafkaConfig;

    public AggregationStarter(EnumMap<KafkaConfig.TopicType, String> topics, KafkaConfig kafkaConfig) {
        this.topics = topics;
        this.kafkaConfig = kafkaConfig;
        this.producer = new KafkaProducer<>(getPropertiesProducerSensor());
        this.consumer = new KafkaConsumer<>(getPropertiesConsumerSensor());
    }

    public void start() {
        final String telemetrySensors = topics.get(KafkaConfig.TopicType.TELEMETRY_SENSORS);
        final String telemetrySnapshots = topics.get(KafkaConfig.TopicType.TELEMETRY_SNAPSHOTS);

        AtomicBoolean running = new AtomicBoolean(true);

        try {
            consumer.subscribe(Collections.singletonList(telemetrySensors));
            log.info("Subscribed to topic: {}", telemetrySensors);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                consumer.wakeup();
            }));

            while (running.get()) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(Duration.ofMillis(100));

                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, SensorEventAvro> record : records) {
                        processEvent(record.value(), telemetrySnapshots);
                    }

                    try {
                        producer.flush();
                        consumer.commitSync();
                    } catch (Exception e) {
                        log.error("Error committing offsets", e);
                    }
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("An unexpected error occurred", e);
        } finally {
            safeClose(producer, consumer);
        }
    }

    private void processEvent(SensorEventAvro event, String telemetrySnapshots) {
        updateState(event).ifPresent(snapshot -> {
            try {
                producer.send(new ProducerRecord<>(telemetrySnapshots, snapshot.getHubId(), snapshot));
                log.info("Snapshot for hubId {} sent to topic {}", snapshot.getHubId(), telemetrySnapshots);
            } catch (Exception e) {
                log.error("Error sending snapshot to topic", e);
            }
        });
    }

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(event.getHubId(), k ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant.now())
                        .setSensorsState(new HashMap<>())
                        .build());

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
        if (oldState != null &&
                !oldState.getTimestamp().isBefore(Instant.ofEpochSecond(event.getTimestamp())) &&
                oldState.getData().equals(event.getPayload())) {
            return Optional.empty();
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp()))
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(Instant.ofEpochSecond(event.getTimestamp()));
        return Optional.of(snapshot);
    }

    private Properties getPropertiesConsumerSensor() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getConsumer().getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getConsumer().getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getConsumer().getValueDeserializer());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConfig.getConsumer().getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getConsumer().getGroupId());
        return config;
    }

    private Properties getPropertiesProducerSensor() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getProducer().getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getProducer().getValueSerializer());
        return config;
    }

    private void safeClose(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    if (resource instanceof KafkaProducer<?, ?>) {
                        ((KafkaProducer<?, ?>) resource).flush();
                    } else if (resource instanceof KafkaConsumer<?, ?>) {
                        ((KafkaConsumer<?, ?>) resource).commitSync();
                    }
                    resource.close();
                } catch (Exception e) {
                    log.error("Failed to close resource", e);
                }
            }
        }
    }
}