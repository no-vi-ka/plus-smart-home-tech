package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.apache.kafka.clients.producer.ProducerRecord;

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
            kafkaTemplate.send(param);
        } catch (Exception e) {
            handleException(param, e);
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