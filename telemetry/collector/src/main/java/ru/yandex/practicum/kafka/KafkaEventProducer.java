package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.exception.errorhandler.KafkaSendException;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaEventProducer implements DisposableBean {
    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, SpecificRecordBase> kafkaTemplate, Producer<String, SpecificRecordBase> kafkaProducer) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRecord(ProducerRecord param) {
        if (!param.isValid()) {
            throw new IllegalArgumentException("Недопустимый ProducerParam: " + param);
        }

        try {
            org.apache.kafka.clients.producer.ProducerRecord<String, SpecificRecordBase> createdRecord = createProducerRecord(param);
            sendKafkaMessage(createdRecord);
        } catch (Exception e) {
            handleException(param, e);
        }
    }

    private org.apache.kafka.clients.producer.ProducerRecord<String, SpecificRecordBase> createProducerRecord(ProducerRecord param) {
        return new org.apache.kafka.clients.producer.ProducerRecord<>(
                param.getTopic(),
                param.getPartition(),
                param.getTimestamp(),
                param.getKey(),
                param.getValue()
        );
    }

    private void sendKafkaMessage(org.apache.kafka.clients.producer.ProducerRecord<String, SpecificRecordBase> record) {
        try {
            kafkaTemplate.send(record).get();
        } catch (ExecutionException e) {
            throw new KafkaSendException("Ошибка при отправке сообщения", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстановите статус прерывания
            throw new KafkaSendException("Ошибка при отправке сообщения", e);
        } catch (Exception e) {
            throw new KafkaSendException("Ошибка при отправке сообщения", e);
        }
    }

    private void handleException(ProducerRecord param, Exception e) {
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