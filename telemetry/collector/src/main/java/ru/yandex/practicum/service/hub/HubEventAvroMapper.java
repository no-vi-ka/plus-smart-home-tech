package ru.yandex.practicum.service.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.hub.device.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.scenario.ScenarioRemovedEvent;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HubEventAvroMapper {
    @Mapping(source = "deviceType", target = "type")
    DeviceAddedEventAvro mapToDeviceAddedEventAvro(DeviceAddedEvent deviceAddedEvent);

    DeviceRemovedEventAvro mapToDeviceRemovedEventAvro(DeviceRemovedEvent deviceRemovedEvent);

    ScenarioAddedEventAvro mapToScenarioAddedEventAvro(ScenarioAddedEvent scenarioAddedEvent);

    ScenarioRemovedEventAvro mapToScenarioRemovedEventAvro(ScenarioRemovedEvent scenarioRemovedEvent);
}
