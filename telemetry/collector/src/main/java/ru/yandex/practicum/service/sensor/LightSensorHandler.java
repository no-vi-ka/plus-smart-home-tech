package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseSensorHandler;

@Service
public class LightSensorHandler extends BaseSensorHandler<LightSensorAvro> {

    public LightSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected LightSensorAvro mapToAvro(SensorEventProto event) {
        var lightEvent = event.getLightSensorEvent();
        return new LightSensorAvro(
                lightEvent.getLinkQuality(),
                lightEvent.getLuminosity()
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }
}