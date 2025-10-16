package ru.practicum.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.Sensor;
import ru.practicum.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository sensorRepository;
    @Override
    public String getType() {
        return DeviceAddedEventAvro.class.getName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro hubEventAvro) {
        Sensor sensor = Mapper.mapToSensor(hubEventAvro, (DeviceAddedEventAvro) hubEventAvro.getPayload());
        if (!sensorRepository.existsByIdInAndHubId(List.of(sensor.getId()), sensor.getHubId())) {
            sensorRepository.save(sensor);
            log.info("added sensor {}", sensor);
        }

    }
}
