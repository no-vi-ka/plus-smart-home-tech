package ru.practicum.event.mapper.sensor;

import ru.practicum.event.model.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();
    SensorEvent toJava(SensorEventProto protoEvent);
}

