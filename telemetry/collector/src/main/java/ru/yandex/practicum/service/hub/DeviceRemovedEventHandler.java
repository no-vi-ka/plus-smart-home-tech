package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseHubHandler;

@Service
public class DeviceRemovedEventHandler extends BaseHubHandler<DeviceRemovedEventAvro> {
    public DeviceRemovedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected DeviceRemovedEventAvro mapToAvro(HubEventProto event) {
        try {
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            throw new IllegalArgumentException("Event must be of type DeviceRemovedEvent");

        } catch (Exception e) {
            throw new RuntimeException("Failed to map HubEvent to DeviceRemovedEventAvro", e);
        }
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }
}
