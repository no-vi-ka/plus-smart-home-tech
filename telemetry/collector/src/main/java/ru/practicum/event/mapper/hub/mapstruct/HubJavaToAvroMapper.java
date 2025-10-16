package ru.practicum.event.mapper.hub.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.device.ConditionType;
import ru.practicum.event.model.hub.device.DeviceAddedEvent;
import ru.practicum.event.model.hub.device.DeviceRemoveEvent;
import ru.practicum.event.model.hub.device.DeviceType;
import ru.practicum.event.model.hub.scenario.ActionsType;
import ru.practicum.event.model.hub.scenario.OperationType;
import ru.practicum.event.model.hub.scenario.ScenarioAddedEvent;
import ru.practicum.event.model.hub.scenario.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HubJavaToAvroMapper {

    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toAvro(DeviceAddedEvent event);

    DeviceRemovedEventAvro toAvro(DeviceRemoveEvent event);

    ScenarioAddedEventAvro toAvro(ScenarioAddedEvent event);

    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event);

    default HubEventAvro toAvro(HubEvent event) {
        Instant ts = event.getTimestamp();
        return switch (event) {
            case DeviceAddedEvent e -> new HubEventAvro(event.getHubId(), ts, toAvro(e));
            case DeviceRemoveEvent e -> new HubEventAvro(event.getHubId(), ts, toAvro(e));
            case ScenarioAddedEvent e -> new HubEventAvro(event.getHubId(), ts, toAvro(e));
            case ScenarioRemovedEvent e -> new HubEventAvro(event.getHubId(), ts, toAvro(e));
            default -> throw new IllegalArgumentException("Unsupported HubEvent type: " + event.getClass());
        };
    }


    default DeviceTypeAvro map(DeviceType type) {
        return type == null ? null : DeviceTypeAvro.valueOf(type.name());
    }

    default ConditionTypeAvro map(ConditionType type) {
        return type == null ? null : ConditionTypeAvro.valueOf(type.name());
    }

    default ConditionOperationAvro map(OperationType type) {
        return type == null ? null : ConditionOperationAvro.valueOf(type.name());
    }

    default ActionTypeAvro map(ActionsType type) {
        return type == null ? null : ActionTypeAvro.valueOf(type.name());
    }
}


