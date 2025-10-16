package ru.practicum.event.mapper.proto;

import ru.practicum.event.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventProtoMapper {
    SensorEventProto.PayloadCase getMessageType();

    SensorEvent map(SensorEventProto event);
}
