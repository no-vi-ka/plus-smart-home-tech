package ru.practicum.event.mapper.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.hub.mapstruct.HubProtoToJavaMapper;
import ru.practicum.event.model.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
@RequiredArgsConstructor
public class HubEventProtoMapper {

    private final HubProtoToJavaMapper mapper;

    public HubEvent toJava(HubEventProto proto) {
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> mapper.deviceAddedToJava(proto);
            case DEVICE_REMOVED -> mapper.deviceRemovedToJava(proto);
            case SCENARIO_ADDED -> mapper.scenarioAddedToJava(proto);
            case SCENARIO_REMOVED -> mapper.scenarioRemovedToJava(proto);
            case PAYLOAD_NOT_SET -> throw new IllegalArgumentException("Payload is not set");
            default -> throw new IllegalArgumentException("Unexpected payload type: " + proto.getPayloadCase());
        };
    }
}


