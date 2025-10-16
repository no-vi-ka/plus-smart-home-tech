package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface EventCollectorService {
    void collectHubEvent(HubEventProto eventProto);

    void collectSensorEvent(SensorEventProto eventProto);
}
