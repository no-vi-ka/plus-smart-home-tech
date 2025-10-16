package ru.practicum.event.mapper.hub.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.hub.HubEventHandler;
import ru.practicum.event.mapper.hub.mapstruct.HubProtoToJavaMapper;
import ru.practicum.event.model.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;


@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final HubProtoToJavaMapper hubEventMapper;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEvent toJava(HubEventProto protoEvent) {
        return hubEventMapper.scenarioAddedToJava(protoEvent);
    }
}
