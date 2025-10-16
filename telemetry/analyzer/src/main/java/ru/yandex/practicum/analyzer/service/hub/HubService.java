package ru.yandex.practicum.analyzer.service.hub;

import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

public interface HubService {
    void addDevice(DeviceAddedEventAvro deviceAddedEventAvro, String hubId);

    void removeDevice(DeviceRemovedEventAvro deviceRemovedEventAvro);

    void addScenario(ScenarioAddedEventAvro scenarioAddedEventAvro, String hubId);

    void removeScenario(ScenarioRemovedEventAvro scenarioRemovedEventAvro, String hubId);
}
