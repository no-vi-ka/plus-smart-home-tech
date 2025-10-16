package ru.yandex.practicum.service.sensor.climate;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.service.sensor.BaseSensorEventHandlerTest;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определяет проверку полей специфичного сенсора
abstract class ClimateHandler extends BaseSensorEventHandlerTest {
    protected ClimateSensorProto sourceProto;

    @Autowired
    public ClimateHandler(SensorEventHandleFactory sensorEventHandleFactory, KafkaProducerConfig kafkaProducerConfig, SensorEventAvroMapper sensorEventAvroMapper, SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory,
                kafkaProducerConfig,
                sensorEventAvroMapper,
                sensorEventProtoMapper,
                sensorEventHandleFactory.getSensorEventHandlerByPayloadCase(SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного сенсора
        ClimateSensorAvro targetAvro = (ClimateSensorAvro) targetBase.getPayload();
        assertEquals(sourceProto.getCo2Level(), targetAvro.getCo2Level());
        assertEquals(sourceProto.getHumidity(), targetAvro.getHumidity());
        assertEquals(sourceProto.getTemperatureC(), targetAvro.getTemperatureC());
    }
}
