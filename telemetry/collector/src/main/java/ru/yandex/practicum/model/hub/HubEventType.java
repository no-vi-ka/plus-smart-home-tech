package ru.yandex.practicum.model.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public enum HubEventType {
    DEVICE_ADDED(HubEventProto.PayloadCase.DEVICE_ADDED),
    DEVICE_REMOVED(HubEventProto.PayloadCase.DEVICE_REMOVED),
    SCENARIO_ADDED(HubEventProto.PayloadCase.SCENARIO_ADDED),
    SCENARIO_REMOVED(HubEventProto.PayloadCase.SCENARIO_REMOVED);

    private final HubEventProto.PayloadCase payloadCase;

    HubEventType(HubEventProto.PayloadCase payloadCase) {
        this.payloadCase = payloadCase;
    }

    public HubEventProto.PayloadCase getProtoPayloadCase() {
        return payloadCase;
    }
}
