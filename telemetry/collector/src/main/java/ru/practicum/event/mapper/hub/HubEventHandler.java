package ru.practicum.event.mapper.hub;

import ru.practicum.event.model.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {
    HubEventProto.PayloadCase getMessageType();
    HubEvent toJava(HubEventProto protoEvent);
}
