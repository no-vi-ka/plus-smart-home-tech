package ru.yandex.practicum.telemetry.collector.service.handler;


import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getType();

    void handle(SensorEventProto event);
}