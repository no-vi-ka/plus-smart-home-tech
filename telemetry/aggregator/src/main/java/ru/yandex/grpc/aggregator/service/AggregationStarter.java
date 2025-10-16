package ru.yandex.grpc.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.grpc.aggregator.configuration.KafkaConsumerConfig;
import ru.yandex.grpc.aggregator.configuration.KafkaProducerConfig;
import ru.yandex.grpc.aggregator.configuration.TopicType;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AggregationStarter {
    private final SnapshotServiceImpl snapshotService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();
    private final Map<TopicType, String> consumerTopics;
    private final Map<TopicType, String> producerTopics;

    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public AggregationStarter(SnapshotServiceImpl snapshotService,
                              KafkaConsumerConfig consumerConfig, KafkaProducerConfig producerConfig) {
        this.snapshotService = snapshotService;
        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.consumerTopics = consumerConfig.getTopics();

        this.producer = new KafkaProducer<>(producerConfig.getProperties());
        this.producerTopics = producerConfig.getTopics();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered. Waking up consumer...");
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(List.of(consumerTopics.get(TopicType.SENSOR_EVENTS)));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = handleRecord(record);
                    Optional<SensorsSnapshotAvro> snapshot = snapshotService.updateState(event);
                    snapshot.ifPresent(this::sendSnapshot);

                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }
            }
        } catch (WakeupException e) {
            log.info("Consumer shutdown detected.");
        } catch (Exception e) {
            log.error("Unexpected error in consumer loop", e);
        } finally {
            try {
                producer.flush();
                if (!currentOffsets.isEmpty()) {
                    try {
                        consumer.commitSync(currentOffsets);
                        log.info("Final offset commit successful: {}", currentOffsets);
                    } catch (Exception e) {
                        log.error("Failed to commit offsets during shutdown: {}", currentOffsets, e);
                    }
                }
            } finally {
                log.info("Closing consumer and producer");
                consumer.close();
                producer.close();
            }
        }
    }

    private SensorEventAvro handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info("Received record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof SensorEventAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (SensorEventAvro) record.value();
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        String topic = producerTopics.get(TopicType.SNAPSHOT_EVENTS);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, snapshot);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send snapshot: {}", snapshot, exception);
            } else {
                log.info("Snapshot sent: topic={}, partition={}, offset={}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
