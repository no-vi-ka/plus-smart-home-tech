package ru.yandex.practicum.service.sensor.switches;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.service.sensor.BaseSensorEventHandlerTest;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определяет проверку полей специфичного сенсора
abstract class SwitchHandler extends BaseSensorEventHandlerTest {
    protected SwitchSensorProto sourceProto;

    @Autowired
    public SwitchHandler(SensorEventHandleFactory sensorEventHandleFactory, KafkaProducerConfig kafkaProducerConfig, SensorEventAvroMapper sensorEventAvroMapper, SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory,
                kafkaProducerConfig,
                sensorEventAvroMapper,
                sensorEventProtoMapper,
                sensorEventHandleFactory.getSensorEventHandlerByPayloadCase(SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного сенсора
        SwitchSensorAvro targetAvro = (SwitchSensorAvro) targetBase.getPayload();
        assertEquals(sourceProto.getState(), targetAvro.getState());
    }
}
