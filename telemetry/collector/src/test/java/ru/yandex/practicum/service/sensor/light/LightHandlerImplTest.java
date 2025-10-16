package ru.yandex.practicum.service.sensor.light;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

// Определяет создание специфичного сенсора и присваивает его в SensorEventProto
@SpringBootTest
public class LightHandlerImplTest extends LightHandler {
    @Autowired
    public LightHandlerImplTest(SensorEventHandleFactory sensorEventHandleFactory,
                                KafkaProducerConfig kafkaProducerConfig,
                                SensorEventAvroMapper sensorEventAvroMapper,
                                SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    protected SensorEventProto createSpecificSensorProto() {
        // Значения для сенсора
        int luminosity = 75;
        int linkQuality = 100;

        LightSensorProto lightSensorProto = LightSensorProto.newBuilder()
                .setLuminosity(luminosity)
                .setLinkQuality(linkQuality)
                .build();
        sourceProto = lightSensorProto;

        // Создание прото Event
        return fillSensorProtoBaseFields(SensorEventProto.newBuilder())
                .setLightSensorEvent(lightSensorProto)
                .build();
    }
}
