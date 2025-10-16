package ru.yandex.practicum.service.sensor.motion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

// Определяет создание специфичного сенсора движения и присваивает его в SensorEventProto
@SpringBootTest
class MotionHandlerImplTest extends MotionHandler {
    @Autowired
    public MotionHandlerImplTest(SensorEventHandleFactory sensorEventHandleFactory,
                                 KafkaProducerConfig kafkaProducerConfig,
                                 SensorEventAvroMapper sensorEventAvroMapper,
                                 SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    protected SensorEventProto createSpecificSensorProto() {
        // Значения для сенсора
        int linkQuality = 100;
        int voltage = 3000;
        boolean motion = true;

        // Создание специфичного сенсора
        MotionSensorProto motionProto = MotionSensorProto.newBuilder()
                .setLinkQuality(linkQuality)
                .setMotion(motion)
                .setVoltage(voltage)
                .build();
        sourceProto = motionProto;

        // Создание прото Event
        return fillSensorProtoBaseFields(SensorEventProto.newBuilder())
                .setMotionSensorEvent(motionProto)
                .build();
    }
}
