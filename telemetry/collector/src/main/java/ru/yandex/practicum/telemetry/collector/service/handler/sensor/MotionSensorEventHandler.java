package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler<MotionSensorAvro> {
    public MotionSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public MotionSensorAvro mapToAvro(SensorEventProto eventProto) {
        MotionSensorProto payload = eventProto.getMotionSensorEvent();
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(payload.getLinkQuality())
                .setMotion(payload.getMotion())
                .setVoltage(payload.getVoltage())
                .build();
    }
}
