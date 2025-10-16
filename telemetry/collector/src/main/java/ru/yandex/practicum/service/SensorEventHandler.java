package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.model.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getSensorEventType();

    void handle(SensorEventProto eventProto);
}
