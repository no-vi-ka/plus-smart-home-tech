package ru.practicum.event.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.event.model.hub.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

public class HubEventMapper {

    public static HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(toHubEventPayloadAvro(hubEvent))
                .build();

    }

    public static SpecificRecordBase toHubEventPayloadAvro(HubEvent hubEvent) {
        switch (hubEvent.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent event = (DeviceAddedEvent) hubEvent;
                return DeviceAddedEventAvro.newBuilder()
                        .setId(event.getId())
                        .setType(toDeviceTypeAvro(event.getDeviceType()))
                        .build();
            }

            case DEVICE_REMOVED -> {
                DeviceRemovedEvent event = (DeviceRemovedEvent) hubEvent;
                return DeviceRemovedEventAvro.newBuilder()
                        .setId(event.getId())
                        .build();
            }

            case SCENARIO_ADDED -> {
                ScenarioAddedEvent event = (ScenarioAddedEvent) hubEvent;
                return ScenarioAddedEventAvro.newBuilder()
                        .setName(event.getName())
                        .setActions(event.getActions()
                                .stream()
                                .map(HubEventMapper::toDeviceActionAvro)
                                .toList())
                        .setConditions(event.getConditions()
                                .stream()
                                .map(HubEventMapper::toScenarioConditionAvro)
                                .toList())
                        .build();
            }

            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent event = (ScenarioRemovedEvent) hubEvent;
                return ScenarioRemovedEventAvro.newBuilder()
                        .setName(event.getName())
                        .build();
            }

            default -> throw new IllegalStateException("Invalid payload: " + hubEvent.getType());
        }
    }

    public static DeviceTypeAvro toDeviceTypeAvro(DeviceType deviceType) {
        return DeviceTypeAvro.valueOf(deviceType.name());
    }

    public static DeviceActionAvro toDeviceActionAvro(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(toActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }

    public static ActionTypeAvro toActionTypeAvro(ActionType actionType) {
        return ActionTypeAvro.valueOf(actionType.name());
    }

    public static ConditionTypeAvro toConditionTypeAvro(ConditionType conditionType) {
        return ConditionTypeAvro.valueOf(conditionType.name());
    }

    public static ConditionOperationAvro toConditionOperationAvro(ConditionOperation conditionOperation) {
        return ConditionOperationAvro.valueOf(conditionOperation.name());
    }

    public static ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition scenarioCondition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(toConditionTypeAvro(scenarioCondition.getConditionType()))
                .setOperation(toConditionOperationAvro(scenarioCondition.getConditionOperation()))
                .setValue(scenarioCondition.getValue())
                .build();
    }
}
