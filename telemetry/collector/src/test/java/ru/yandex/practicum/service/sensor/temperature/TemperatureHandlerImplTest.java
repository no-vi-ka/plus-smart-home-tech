package ru.yandex.practicum.service.sensor.temperature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

// Определяет создание специфичного температурного сенсора и присваивает его в SensorEventProto
@SpringBootTest
class TemperatureHandlerImplTest extends TemperatureHandler {
    @Autowired
    public TemperatureHandlerImplTest(SensorEventHandleFactory sensorEventHandleFactory,
                                      KafkaProducerConfig kafkaProducerConfig,
                                      SensorEventAvroMapper sensorEventAvroMapper,
                                      SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    protected SensorEventProto createSpecificSensorProto() {
        // Значения для температурного сенсора
        int temperatureC = 25;
        int temperatureF = 77;

        // Создание специфичного сенсора
        TemperatureSensorProto temperatureProto = TemperatureSensorProto.newBuilder()
                .setTemperatureC(temperatureC)
                .setTemperatureF(temperatureF)
                .build();
        sourceProto = temperatureProto;

        // Создание прото Event
        return fillSensorProtoBaseFields(SensorEventProto.newBuilder())
                .setTemperatureSensorEvent(temperatureProto)
                .build();
    }
}
