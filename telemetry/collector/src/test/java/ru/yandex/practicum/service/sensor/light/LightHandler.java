package ru.yandex.practicum.service.sensor.light;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.service.sensor.BaseSensorEventHandlerTest;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определяет проверку полей специфичного сенсора
abstract class LightHandler extends BaseSensorEventHandlerTest {
    protected LightSensorProto sourceProto;

    @Autowired
    public LightHandler(SensorEventHandleFactory sensorEventHandleFactory, KafkaProducerConfig kafkaProducerConfig, SensorEventAvroMapper sensorEventAvroMapper, SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory,
                kafkaProducerConfig,
                sensorEventAvroMapper,
                sensorEventProtoMapper,
                sensorEventHandleFactory.getSensorEventHandlerByPayloadCase(SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного сенсора
        LightSensorAvro targetAvro = (LightSensorAvro) targetBase.getPayload();
        assertEquals(sourceProto.getLinkQuality(), targetAvro.getLinkQuality());
        assertEquals(sourceProto.getLuminosity(), targetAvro.getLuminosity());
    }
}