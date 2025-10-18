package ru.yandex.practicum.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Component
@RequiredArgsConstructor
public class DeviceAdded implements HubEventHandler {

    private final SensorRepository sensorRepository;

    @Override
    public String getMessageType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        DeviceAddedEventAvro deviceAddedEvent = (DeviceAddedEventAvro) hubEvent.getPayload();
        Sensor sensor = Sensor.builder()
                        .id(deviceAddedEvent.getId())
                        .hubId(hubEvent.getHubId())
                        .build();
        sensorRepository.save(sensor);

    }
}
