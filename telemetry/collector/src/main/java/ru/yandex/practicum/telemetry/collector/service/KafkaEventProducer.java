package ru.yandex.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.configuration.TopicType;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
@Slf4j
public class KafkaEventProducer implements AutoCloseable {
    protected final KafkaProducer<String, SpecificRecordBase> kafkaProducer;
    protected final Map<TopicType, String> topics;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.kafkaProducer = new KafkaProducer<>(kafkaConfig.getProducer().getProperties());
        this.topics = kafkaConfig.getTopics();
    }

    public void send(SpecificRecordBase event, String hubId, Instant timestamp, TopicType topic) {
        String topicName = topics.get(topic);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topicName,
                null,
                timestamp.toEpochMilli(),
                hubId,
                event
        );

        log.info("<== Send message: {} to topic: {}", record, topic);
        kafkaProducer.send(record);
    }

    @Override
    public void close() {
        kafkaProducer.flush();
        kafkaProducer.close(Duration.ofSeconds(10));
    }
}
