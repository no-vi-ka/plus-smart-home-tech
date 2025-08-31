package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.exception.errorhandler.KafkaSendException;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaEventProducer implements DisposableBean {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate, Producer<String, SpecificRecordBase> kafkaProducer) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRecord(ProducerRecord<String, SpecificRecordBase> param) {
        if (!(param.topic() != null && param.timestamp() != null && param.key() != null && param.value() != null)) {
            throw new IllegalArgumentException("Недопустимый ProducerParam: " + param);
        }

        try {
            ProducerRecord<String, SpecificRecordBase> createdRecord = createProducerRecord(param.topic(), param.timestamp(), param.key(), param.value());
            sendKafkaMessage(createdRecord);
        } catch (Exception e) {
            handleException(param, e);
        }
    }

    private ProducerRecord<String, SpecificRecordBase> createProducerRecord(String topic, Long timestamp, String key, SpecificRecordBase value) {
        return new ProducerRecord<>(topic, null, timestamp, key, value);
    }

    private void sendKafkaMessage(ProducerRecord<String, SpecificRecordBase> record) {
        try {
            kafkaTemplate.send(record);
        } catch (Exception e) {
            throw new KafkaSendException("Ошибка при отправке сообщения", e);
        }
    }

    private void handleException(ProducerRecord<String, SpecificRecordBase> param, Exception e) {
        log.error("Ошибка при отправке сообщения для param={}", param, e);
    }

    @Override
    public void destroy() {
        try {
            kafkaTemplate.flush();
            log.info("KafkaEventProducer корректно остановлен");
        } catch (Exception e) {
            log.error("Ошибка при закрытии KafkaEventProducer", e);
            throw e;
        }
    }
}