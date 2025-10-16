package ru.yandex.practicum.service.sensor.motion;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.service.sensor.BaseSensorEventHandlerTest;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определяет проверку полей специфичного сенсора
abstract class MotionHandler extends BaseSensorEventHandlerTest {
    protected MotionSensorProto sourceProto;

    @Autowired
    public MotionHandler(SensorEventHandleFactory sensorEventHandleFactory, KafkaProducerConfig kafkaProducerConfig, SensorEventAvroMapper sensorEventAvroMapper, SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory,
                kafkaProducerConfig,
                sensorEventAvroMapper,
                sensorEventProtoMapper,
                sensorEventHandleFactory.getSensorEventHandlerByPayloadCase(SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного сенсора
        MotionSensorAvro targetAvro = (MotionSensorAvro) targetBase.getPayload();
        assertEquals(sourceProto.getLinkQuality(), targetAvro.getLinkQuality());
        assertEquals(sourceProto.getMotion(), targetAvro.getMotion());
        assertEquals(sourceProto.getVoltage(), targetAvro.getVoltage());
    }
}
