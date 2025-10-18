package ru.yandex.practicum.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Component
@RequiredArgsConstructor
public class DeviceRemoved implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public String getMessageType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        DeviceRemovedEventAvro deviceAddedEvent = (DeviceRemovedEventAvro) hubEvent.getPayload();
        sensorRepository.deleteByIdAndHubId(deviceAddedEvent.getId(), hubEvent.getHubId());
     }
}
