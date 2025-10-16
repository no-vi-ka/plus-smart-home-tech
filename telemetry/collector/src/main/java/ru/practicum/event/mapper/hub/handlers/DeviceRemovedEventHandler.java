package ru.practicum.event.mapper.hub.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.hub.HubEventHandler;
import ru.practicum.event.mapper.hub.mapstruct.HubProtoToJavaMapper;
import ru.practicum.event.model.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final HubProtoToJavaMapper hubProtoToJavaMapper;
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public HubEvent toJava(HubEventProto protoEvent) {
        return hubProtoToJavaMapper.deviceRemovedToJava(protoEvent);
    }
}
