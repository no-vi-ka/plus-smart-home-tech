package ru.practicum.event.mapper.hub.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.hub.HubEventHandler;
import ru.practicum.event.mapper.hub.mapstruct.HubProtoToJavaMapper;
import ru.practicum.event.model.HubEvent;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final HubProtoToJavaMapper mapper;


    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public HubEvent toJava(HubEventProto proto) {
        return mapper.scenarioRemovedToJava(proto);
    }
}

