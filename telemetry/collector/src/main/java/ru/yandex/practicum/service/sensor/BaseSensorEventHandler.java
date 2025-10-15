package ru.yandex.practicum.service.sensor;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.SensorEventHandler;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final KafkaConfig kafkaConfig;
    protected final KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate;
    private final String sensors = "sensors";

    protected abstract T mapToAvro(SensorEventProto eventProto);

    @Override
    public void handle(SensorEventProto eventProto) {
        if (!eventProto.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException(String.format("Неизвестный тип события: %s", eventProto.getPayloadCase()));
        }

        T payload = mapToAvro(eventProto);

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(eventProto.getId())
                .setHubId(eventProto.getHubId())
                .setTimestamp(mapToInstant(eventProto.getTimestamp()))
                .setPayload(payload)
                .build();

        kafkaTemplate.send(kafkaConfig.getTopics().get(sensors), eventAvro);
    }

    protected Instant mapToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos()
        );
    }

    protected void send(String topic, SensorEventAvro event) {
        try (Producer<Void, SensorEventAvro> producer = new KafkaProducer<>(kafkaConfig.getProducerProperties())){
            while (true) {
                ProducerRecord<Void, SensorEventAvro> record = new ProducerRecord<>(topic, event);
                producer.send(record);
            }
        } catch (Exception e) {
            log.error("Ошибка отправки данных {}", topic);
        }
    }
}
