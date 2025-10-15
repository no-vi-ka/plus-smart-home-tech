package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.HubEventHandler;

@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro eventAvro) {
        DeviceRemovedEventAvro deviceRemovedEvent = (DeviceRemovedEventAvro) eventAvro.getPayload();
        sensorRepository.deleteByIdAndHubId(deviceRemovedEvent.getId(), eventAvro.getHubId());
    }

    @Override
    public String getMessageType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }

}
