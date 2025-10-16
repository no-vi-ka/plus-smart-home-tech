package ru.practicum.event.mapper.proto;

import ru.practicum.event.model.hub.HubEvent;
import ru.practicum.event.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface HubEventProtoMapper {
    HubEventProto.PayloadCase getMessageType();

    HubEvent map(HubEventProto event);
}
