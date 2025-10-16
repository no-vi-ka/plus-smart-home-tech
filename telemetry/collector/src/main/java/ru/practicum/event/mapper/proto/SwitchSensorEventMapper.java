package ru.practicum.event.mapper.proto;

import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.SensorEventMapper;
import ru.practicum.event.model.sensor.SensorEvent;
import ru.practicum.event.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;

import java.time.Instant;

@Component
public class SwitchSensorEventMapper implements SensorEventProtoMapper {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        SwitchSensorProto sensorEvent = event.getSwitchSensorEvent();

        SwitchSensorEvent switchSensorEvent = SwitchSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .state(sensorEvent.getState())
                .build();

        System.out.println("switchSensorEvent = " + switchSensorEvent);
        return switchSensorEvent;
    }
}
