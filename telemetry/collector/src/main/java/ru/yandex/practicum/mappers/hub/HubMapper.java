package ru.yandex.practicum.mappers.hub;

import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.*;

public class HubMapper {
    public static DeviceAddedEventAvro mapToDeviceAddedEventAvro(DeviceAddedEvent event) {
        return DeviceAddedEventAvro.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeAvro.valueOf(event.getDeviceType().toString()))
                .build();
    }

    public static DeviceRemovedEventAvro mapToDeviceRemovedEventAvro(DeviceRemovedEvent event) {
        return DeviceRemovedEventAvro.newBuilder()
                .setId(event.getId())
                .build();
    }

    public static ScenarioAddedEventAvro mapToScenarioAddedEventAvro(ScenarioAddedEvent event) {
        return ScenarioAddedEventAvro.newBuilder()
                .setActions(event.getActions().stream()
                        .map(HubMapper::mapToDeviceActionAvro)
                        .toList()
                )
                .setConditions(event.getConditions().stream()
                        .map(HubMapper::mapToScenarioConditionAvro)
                        .toList()
                )
                .setName(event.getName())
                .build();
    }

    public static ScenarioRemovedEventAvro mapToScenarioRemovedEventAvro(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }


    private static ScenarioConditionAvro mapToScenarioConditionAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setType(ConditionTypeAvro.valueOf(condition.getType().toString()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().toString()))
                .setSensorId(condition.getSensorId())
                .setValue(condition.getValue())
                .build();
    }

    private static DeviceActionAvro mapToDeviceActionAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().toString()))
                .setValue(action.getValue())
                .build();
    }
}
