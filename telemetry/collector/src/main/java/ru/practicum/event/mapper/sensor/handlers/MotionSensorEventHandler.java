package ru.practicum.event.mapper.sensor.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.sensor.SensorEventHandler;
import ru.practicum.event.mapper.sensor.mapstruct.SensorProtoToJavaMapper;
import ru.practicum.event.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final SensorProtoToJavaMapper sensorProtoToJavaMapper;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }


    @Override
    public MotionSensorEvent toJava(SensorEventProto protoEvent) {
        return sensorProtoToJavaMapper.motionToJava(protoEvent);
    }
}

