package ru.yandex.practicum.service.kafka;

public interface KafkaEventProducer {
    void send(ProducerSendParam param);
}
