package ru.yandex.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorHandler<T extends SpecificRecordBase> implements SensorHandler {

    protected final Config kafkaConfig;
    protected final Producer kafkaEventProducer;
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";

    protected abstract T mapToAvro(SensorEventProto event);

    @Override
    public void handle(SensorEventProto event) {
        T avroEvent = mapToAvro(event);
        if (avroEvent == null) {
            log.error("Failed to map event to Avro: {}", event);
            return;
        }
        try {
            log.info("Sending sensor event {} to topic {}", getMessageType(), SENSOR_TOPIC);
            kafkaEventProducer.send(SENSOR_TOPIC, event.getId(), avroEvent);
        } catch (Exception e) {
            log.error("Error sending sensor event to Kafka topic {}: {}", SENSOR_TOPIC, e.getMessage(), e);
        }
    }
}