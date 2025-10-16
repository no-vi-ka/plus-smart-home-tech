package ru.yandex.practicum.service.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.model.hub.BaseHubEvent;
import ru.yandex.practicum.model.hub.device.DeviceAction;
import ru.yandex.practicum.model.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioCondition;
import ru.yandex.practicum.model.hub.scenario.ScenarioConditionType;
import ru.yandex.practicum.model.hub.scenario.ScenarioOperationType;
import ru.yandex.practicum.model.hub.scenario.ScenarioRemovedEvent;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HubEventProtoMapper {
    @Mapping(source = "type", target = "deviceType")
    @ValueMapping(source = "UNRECOGNIZED", target = "MOTION_SENSOR")
    DeviceAddedEvent mapToDeviceAddedEvent(DeviceAddedEventProto proto);

    DeviceRemovedEvent mapToDeviceRemovedEvent(DeviceRemovedEventProto proto);

    @Mapping(source = "conditionsList", target = "conditions")
    @Mapping(source = "actionsList", target = "actions")
    ScenarioAddedEvent mapToScenarioAddedEvent(ScenarioAddedEventProto proto);

    @Mapping(source = "operation", target = "operation")
    @Mapping(source = "type", target = "type")
    @Mapping(target = "value", expression = "java(mapScenarioConditionValue(proto))")
    ScenarioCondition mapToScenarioCondition(ScenarioConditionProto proto);

    @ValueMapping(source = "UNRECOGNIZED", target = "EQUALS")
    ScenarioOperationType mapToScenarioOperationType(ConditionOperationProto proto);

    @ValueMapping(source = "UNRECOGNIZED", target = "MOTION")
    ScenarioConditionType mapToScenarioConditionType(ConditionTypeProto proto);

    @Mapping(source = "type", target = "type")
    @ValueMapping(source = "UNRECOGNIZED", target = "ACTIVATE")
    DeviceAction mapToDeviceAction(DeviceActionProto proto);

    ScenarioRemovedEvent mapToScenarioRemovedEvent(ScenarioRemovedEventProto proto);

    default Object mapScenarioConditionValue(ScenarioConditionProto proto) {
        if (proto.hasBoolValue()) {
            return proto.getBoolValue();
        } else if (proto.hasIntValue()) {
            return proto.getIntValue();
        } else {
            return null;
        }
    }

    default BaseHubEvent mapBaseFields(BaseHubEvent event, HubEventProto hubEventProto) {
        event.setHubId(hubEventProto.getHubId());

        long seconds = hubEventProto.getTimestamp().getSeconds();
        int nanos = hubEventProto.getTimestamp().getNanos();

        event.setTimestamp(Instant.ofEpochSecond(seconds, nanos));
        return event;
    }
}
