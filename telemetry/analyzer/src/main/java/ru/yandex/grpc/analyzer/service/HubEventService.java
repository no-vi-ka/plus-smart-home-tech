package ru.yandex.grpc.analyzer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.grpc.analyzer.dal.service.ScenarioService;
import ru.yandex.grpc.analyzer.dal.service.SensorService;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Service
@RequiredArgsConstructor
public class HubEventService {
    private final SensorService sensorService;
    private final ScenarioService scenarioService;

    @Transactional
    public void handleEvent(HubEventAvro hubEvent) {
        String hubId = hubEvent.getHubId();
        Object payload = hubEvent.getPayload();

        switch (payload) {
            case DeviceAddedEventAvro event -> sensorService.addSensor(event.getId(), hubId);
            case DeviceRemovedEventAvro event -> sensorService.removeSensor(event.getId());
            case ScenarioAddedEventAvro event -> scenarioService.addScenario(event, hubId);
            case ScenarioRemovedEventAvro event -> scenarioService.removeScenario(event.getName(), hubId);
            default -> throw new IllegalArgumentException("Unexpected event type: " + payload.getClass());
        }
    }
}
