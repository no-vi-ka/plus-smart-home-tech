package ru.practicum.event.mapper.sensor.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.sensor.SensorEventHandler;
import ru.practicum.event.mapper.sensor.mapstruct.SensorProtoToJavaMapper;
import ru.practicum.event.model.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {
    private final SensorProtoToJavaMapper sensorProtoToJavaMapper;
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public SensorEvent toJava(SensorEventProto protoEvent) {
        return sensorProtoToJavaMapper.temperatureToJava(protoEvent);
    }
}
