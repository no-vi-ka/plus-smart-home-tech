package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {
    HubEventProto.PayloadCase getType();

    void handle(HubEventProto event);
}
