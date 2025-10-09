package ru.yandex.practicum.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubHandler {
    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);
}
