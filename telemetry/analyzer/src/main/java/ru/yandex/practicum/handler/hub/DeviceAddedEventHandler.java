package ru.yandex.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.HubEventMapper;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;

    @Override
    public String getHubEventType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();

        Optional<Sensor> oldSensorOpt = sensorRepository.findByIdAndHubId(payload.getId(), event.getHubId());
        if (oldSensorOpt.isEmpty()) {
            log.info("{}: Достаем из event, payload: {}", DeviceAddedEventHandler.class.getSimpleName(), payload);
            Sensor sensor = HubEventMapper.mapToSensor(payload.getId(), event.getHubId());
            log.info("{}: Сохраняем в БД sensor: {}", DeviceAddedEventHandler.class.getSimpleName(), sensor);
            sensorRepository.save(sensor);
        } else {
            log.info("Указанное устройство уже существует: {}", oldSensorOpt.get());
        }
    }
}
