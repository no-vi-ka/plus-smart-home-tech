package ru.yandex.practicum.service.kafka;

import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaProducerConfig;

@Component
public class KafkaEventProducerImpl implements KafkaEventProducer {
    private final Producer<String, SpecificRecordBase> producer;

    private KafkaEventProducerImpl(KafkaProducerConfig kafkaProducerConfig) {
        this.producer = new KafkaProducer<>(kafkaProducerConfig.getProperties());
    }

    public void send(ProducerSendParam param) {
        if (!param.isValid()) {
            throw new IllegalArgumentException("invalid ProducerSendParam=" + param);
        }

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                param.getTopic(),
                param.getPartition(),
                param.getTimestamp(),
                param.getKey(),
                param.getValue());

        producer.send(record);
    }

    @PreDestroy
    private void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
