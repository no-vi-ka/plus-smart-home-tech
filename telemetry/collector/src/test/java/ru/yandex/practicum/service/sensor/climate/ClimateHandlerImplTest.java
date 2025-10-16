package ru.yandex.practicum.service.sensor.climate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

// Определяет создание специфичного сенсора и присваивает его в SensorEventProto
@SpringBootTest
class ClimateHandlerImplTest extends ClimateHandler {
    @Autowired
    public ClimateHandlerImplTest(SensorEventHandleFactory sensorEventHandleFactory,
                                  KafkaProducerConfig kafkaProducerConfig,
                                  SensorEventAvroMapper sensorEventAvroMapper,
                                  SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    protected SensorEventProto createSpecificSensorProto() {
        // Значения для сенсора
        int temperatureC = 25;
        int humidity = 75;
        int co2 = 5;

        // Создание специфичного сенсора
        ClimateSensorProto climateProto = ClimateSensorProto.newBuilder()
                .setCo2Level(co2)
                .setHumidity(humidity)
                .setTemperatureC(temperatureC)
                .build();
        sourceProto = climateProto;

        // Создание прото Event
        return fillSensorProtoBaseFields(SensorEventProto.newBuilder())
                .setClimateSensorEvent(climateProto)
                .build();
    }
}
