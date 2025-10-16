package ru.yandex.practicum.service.sensor.switches;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.service.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.sensor.SensorEventProtoMapper;
import ru.yandex.practicum.util.SensorEventHandleFactory;

// Определяет создание специфичного сенсора переключателя и присваивает его в SensorEventProto
@SpringBootTest
class SwitchHandlerImplTest extends SwitchHandler {
    @Autowired
    public SwitchHandlerImplTest(SensorEventHandleFactory sensorEventHandleFactory,
                                 KafkaProducerConfig kafkaProducerConfig,
                                 SensorEventAvroMapper sensorEventAvroMapper,
                                 SensorEventProtoMapper sensorEventProtoMapper) {
        super(sensorEventHandleFactory, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    protected SensorEventProto createSpecificSensorProto() {
        // Значения для сенсора
        boolean state = true;

        // Создание специфичного сенсора
        SwitchSensorProto switchProto = SwitchSensorProto.newBuilder()
                .setState(state)
                .build();
        sourceProto = switchProto;

        // Создание прото Event
        return fillSensorProtoBaseFields(SensorEventProto.newBuilder())
                .setSwitchSensorEvent(switchProto)
                .build();
    }
}
