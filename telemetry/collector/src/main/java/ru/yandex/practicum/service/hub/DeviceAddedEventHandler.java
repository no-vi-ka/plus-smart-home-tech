package ru.yandex.practicum.service.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseHubHandler;

@Service
@Slf4j
public class DeviceAddedEventHandler extends BaseHubHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto event) {
        if (event == null) {
            log.error("Received null HubEventProto");
            throw new IllegalArgumentException("Event cannot be null");
        }

        if (event.getPayloadCase() != HubEventProto.PayloadCase.DEVICE_ADDED) {
            log.error("Expected DEVICE_ADDED event, got: {}", event.getPayloadCase());
            throw new IllegalArgumentException(
                    "Event must be of type DeviceAddedEvent, got: " + event.getPayloadCase());
        }

        DeviceAddedEventProto deviceAdded = event.getDeviceAdded();
        if (deviceAdded == null) {
            log.error("DeviceAddedEventProto is null for event: {}", event);
            throw new IllegalArgumentException("DeviceAddedEventProto is null");
        }

        log.debug("Mapping DeviceAddedEvent: hub_id={}, timestamp={}, device_id={}, type={}",
                event.getHubId(), event.getTimestamp(), deviceAdded.getId(), deviceAdded.getType());

        DeviceAddedEventAvro avro = new DeviceAddedEventAvro();
        avro.setId(event.getHubId());
        avro.setType(DeviceTypeAvro.valueOf(deviceAdded.getType().toString()));

        return avro;
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }
}