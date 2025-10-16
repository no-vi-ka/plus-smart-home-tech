package ru.practicum.event.mapper.hub.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.hub.HubEventHandler;
import ru.practicum.event.mapper.hub.mapstruct.HubProtoToJavaMapper;
import ru.practicum.event.model.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final HubProtoToJavaMapper mapper;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public HubEvent toJava(HubEventProto proto) {
        return mapper.deviceAddedToJava(proto);
    }
}
