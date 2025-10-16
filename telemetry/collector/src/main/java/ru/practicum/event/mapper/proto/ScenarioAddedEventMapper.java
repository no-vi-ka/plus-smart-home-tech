package ru.practicum.event.mapper.proto;

import org.springframework.stereotype.Component;
import ru.practicum.event.model.hub.*;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;

import java.time.Instant;

@Component
public class ScenarioAddedEventMapper implements HubEventProtoMapper {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEvent map(HubEventProto event) {
        ScenarioAddedEventProto hubEvent = event.getScenarioAdded();

        ScenarioAddedEvent scenarioAddedEvent = ScenarioAddedEvent.builder()
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .name(hubEvent.getName())
                .actions(hubEvent.getActionList().stream().map(this::map).toList())
                .conditions(hubEvent.getConditionList().stream().map(this::map).toList())
                .build();
        return scenarioAddedEvent;
    }

    private DeviceAction map(DeviceActionProto deviceActionProto) {
        return DeviceAction.builder()
                .sensorId(deviceActionProto.getSensorId())
                .type(ActionType.valueOf(deviceActionProto.getType().name()))
                .value(deviceActionProto.getValue())
                .build();
    }

    private ScenarioCondition map(ScenarioConditionProto scenarioConditionProto) {
        return ScenarioCondition.builder()
                .sensorId(scenarioConditionProto.getSensorId())
                .conditionType(ConditionType.valueOf(scenarioConditionProto.getType().name()))
                .conditionOperation(ConditionOperation.valueOf(scenarioConditionProto.getOperation().name()))
                .value(scenarioConditionProto.getIntValue())
                .build();
    }
}
