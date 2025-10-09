package ru.yandex.practicum.kafkaConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
@Slf4j
public class Producer {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    public <T extends SpecificRecordBase> void send(String topic, String key, T event) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, key, event);
        log.info("Sending event to topic: {} with key: {}", topic, key);

        try {
            Future<RecordMetadata> future = kafkaProducer.send(record);
            future.get();
            log.info("Message successfully posted in topic: {}, partition: {}, offset: {}",
                    future.get().topic(), future.get().partition(), future.get().offset());
        } catch (Exception e) {
            log.error("Error sending message to topic: {}", topic, e);
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }


    public void close() {
        try {
            if (kafkaProducer != null) {
                kafkaProducer.flush();
                kafkaProducer.close(Duration.ofMillis(1000));
            }
        } catch (Exception e) {
            log.error("Error while closing Kafka producer", e);
        }
    }
}