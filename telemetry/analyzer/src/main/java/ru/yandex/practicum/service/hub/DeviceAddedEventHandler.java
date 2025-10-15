package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;
import ru.yandex.practicum.service.HubEventHandler;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public String getMessageType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro eventAvro) {
        sensorRepository.save(buildSensor(eventAvro));
    }

    private Sensor buildSensor(HubEventAvro eventAvro) {
        DeviceAddedEventAvro deviceAddedEvent = (DeviceAddedEventAvro) eventAvro.getPayload();
        return Sensor.builder()
                .id(deviceAddedEvent.getId())
                .hubId(eventAvro.getHubId())
                .build();
    }

}
