package ru.yandex.practicum.grpc.eventhandlers.sensors;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);
}
