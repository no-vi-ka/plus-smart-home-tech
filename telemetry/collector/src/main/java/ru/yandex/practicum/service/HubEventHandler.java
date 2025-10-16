package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getHubEventType();

    void handle(HubEventProto eventProto);
}
