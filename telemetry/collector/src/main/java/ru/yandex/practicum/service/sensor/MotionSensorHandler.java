package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseSensorHandler;

@Service
public class MotionSensorHandler extends BaseSensorHandler<MotionSensorAvro> {
    public MotionSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected MotionSensorAvro mapToAvro(SensorEventProto event) {
        var motionEvent = event.getMotionSensorEvent();

        return new MotionSensorAvro(
                motionEvent.getLinkQuality(),
                motionEvent.getMotion(),
                motionEvent.getVoltage()
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}
