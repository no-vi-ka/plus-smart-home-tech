package ru.yandex.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig;
import ru.yandex.practicum.telemetry.collector.config.KafkaConfig.TopicType;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;

@Slf4j
@Component
public class KafkaEventProducer implements AutoCloseable {

    protected final KafkaProducer<String, SpecificRecordBase> producer;
    protected final EnumMap<TopicType, String> topics;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.topics = kafkaConfig.getProducer().getTopics();
        this.producer = new KafkaProducer<>(kafkaConfig.getProducer().getProperties());
    }

    public void send(SpecificRecordBase event, String hubId, Instant timeStamp, KafkaConfig.TopicType topicType) {
        String topic = topics.get(topicType);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timeStamp.toEpochMilli(),
                hubId,
                event
        );

        log.trace("Сохраняю событие {}, связанное с хабом {}, в топик {}",
                event.getClass().getSimpleName(), hubId, topic);

        log.info("<== Json: {}", event);
        producer.send(record);
    }

    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}
