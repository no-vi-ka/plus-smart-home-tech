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

    public void sendRecord(String topic, Long timestamp, String key, SpecificRecordBase value) {
        if (!(topic != null && timestamp != null && key != null && value != null)) {
            throw new IllegalArgumentException("Недопустимое содержимое ProducerParam: " + topic + ", " + timestamp + ", " + timestamp + ", " + timestamp);
        }

        try {
            ProducerRecord<String, SpecificRecordBase> createdRecord = createProducerRecord(topic, timestamp, key, value);
            sendKafkaMessage(createdRecord);
        } catch (Exception e) {
            handleException(topic, timestamp, key, value, e);
        }
    }

    private ProducerRecord<String, SpecificRecordBase> createProducerRecord(String topic, Long timestamp, String key, SpecificRecordBase value) {
        return new ProducerRecord<>(topic, null, timestamp, key, value);
    }

    private void sendKafkaMessage(ProducerRecord<String, SpecificRecordBase> record) {
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

    private void handleException(String topic, Long timestamp, String key, SpecificRecordBase value, Exception e) {
        log.error("Ошибка при отправке сообщения для содержимого ProducerParam: " + topic + ", " + timestamp + ", " + timestamp + ", " + timestamp, e);
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