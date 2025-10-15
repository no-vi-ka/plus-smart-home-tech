package ru.yandex.practicum.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

import java.time.Instant;

@Component
public class SwitchSensorEventHandler extends SensorProtoHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    public SensorEventAvro mapToAvro(SensorEventProto eventProto) {
        SwitchSensorProto switchSensorProto = eventProto.getSwitchSensorEvent();
        SwitchSensorAvro switchSensorAvro = SwitchSensorAvro.newBuilder()
                .setState(switchSensorProto.getState())
                .build();

        return SensorEventAvro.newBuilder()
                .setId(eventProto.getId())
                .setHubId(eventProto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(eventProto.getTimestamp().getSeconds(),
                        eventProto.getTimestamp().getNanos()))
                .setPayload(switchSensorAvro)
                .build();
    }
}
