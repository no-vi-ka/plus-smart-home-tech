package ru.practicum.event.mapper.proto;

import org.springframework.stereotype.Component;
import ru.practicum.event.model.hub.DeviceAddedEvent;
import ru.practicum.event.model.hub.DeviceType;
import ru.practicum.event.model.hub.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@Component
public class DeviceAddedEventMapper implements HubEventProtoMapper {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public HubEvent map(HubEventProto event) {
        DeviceAddedEventProto hubEvent = event.getDeviceAdded();

        DeviceAddedEvent deviceAddedEvent = DeviceAddedEvent.builder()
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .id(hubEvent.getId())
                .deviceType(DeviceType.valueOf(hubEvent.getType().name()))
                .build();
        return deviceAddedEvent;
    }
}
