package ru.practicum.event.mapper.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.sensor.mapstruct.SensorProtoToJavaMapper;
import ru.practicum.event.model.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
@RequiredArgsConstructor
public class SensorEventProtoMapper {

    private final SensorProtoToJavaMapper sensorProtoToJavaMapper;

    public SensorEvent toJava(SensorEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case CLIMATE_SENSOR_EVENT -> sensorProtoToJavaMapper.climateToJava(proto);
            case LIGHT_SENSOR_EVENT -> sensorProtoToJavaMapper.lightToJava(proto);
            case MOTION_SENSOR_EVENT -> sensorProtoToJavaMapper.motionToJava(proto);
            case SWITCH_SENSOR_EVENT -> sensorProtoToJavaMapper.switchToJava(proto);
            case TEMPERATURE_SENSOR_EVENT -> sensorProtoToJavaMapper.temperatureToJava(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload is not set");
            default -> throw new IllegalArgumentException("Unexpected payload type: " + proto.getPayloadCase());
        };
    }
}
