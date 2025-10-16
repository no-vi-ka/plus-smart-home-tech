package ru.practicum.event.mapper.hub.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.event.model.hub.device.ConditionType;
import ru.practicum.event.model.hub.device.DeviceAddedEvent;
import ru.practicum.event.model.hub.device.DeviceRemoveEvent;
import ru.practicum.event.model.hub.device.DeviceType;
import ru.practicum.event.model.hub.scenario.*;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HubProtoToJavaMapper {


    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "id", source = "deviceAdded.id")
    @Mapping(target = "deviceType", source = "deviceAdded.type")
    DeviceAddedEvent deviceAddedToJava(HubEventProto proto);


    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "id", source = "deviceRemoved.id")
    DeviceRemoveEvent deviceRemovedToJava(HubEventProto proto);


    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "name", source = "scenarioAdded.name")
    @Mapping(target = "conditions", source = "scenarioAdded.conditionList")
    @Mapping(target = "actions", source = "scenarioAdded.actionList")
    ScenarioAddedEvent scenarioAddedToJava(HubEventProto proto);


    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "timestamp", expression = "java(toInstant(proto.getTimestamp()))")
    @Mapping(target = "name", source = "scenarioRemoved.name")
    ScenarioRemovedEvent scenarioRemovedToJava(HubEventProto proto);


    default Conditions toJava(ScenarioConditionProto proto) {
        if (proto == null) return null;
        Conditions c = new Conditions();
        c.setSensorId(proto.getSensorId());
        c.setType(map(proto.getType()));
        c.setOperation(map(proto.getOperation()));


        if (proto.hasBoolValue()) {
            c.setValue(proto.getBoolValue() ? 1 : 0);
        } else if (proto.hasIntValue()) {
            c.setValue(proto.getIntValue());
        } else {
            c.setValue(null);
        }
        return c;
    }


    default Actions toJava(DeviceActionProto proto) {
        if (proto == null) return null;
        Actions a = new Actions();
        a.setSensorId(proto.getSensorId());
        a.setType(map(proto.getType()));
        a.setValue(proto.hasValue() ? proto.getValue() : null);
        return a;
    }


    default DeviceType map(DeviceTypeProto proto) {
        return proto == null || proto == DeviceTypeProto.UNRECOGNIZED
                ? null : DeviceType.valueOf(proto.name());
    }

    default ConditionType map(ConditionTypeProto proto) {
        return proto == null || proto == ConditionTypeProto.UNRECOGNIZED
                ? null : ConditionType.valueOf(proto.name());
    }

    default OperationType map(ConditionOperationProto proto) {
        return proto == null || proto == ConditionOperationProto.UNRECOGNIZED
                ? null : OperationType.valueOf(proto.name());
    }

    default ActionsType map(ActionTypeProto proto) {
        return proto == null || proto == ActionTypeProto.UNRECOGNIZED
                ? null : ActionsType.valueOf(proto.name());
    }


    default Instant toInstant(com.google.protobuf.Timestamp ts) {
        return ts == null ? null : Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }
}



