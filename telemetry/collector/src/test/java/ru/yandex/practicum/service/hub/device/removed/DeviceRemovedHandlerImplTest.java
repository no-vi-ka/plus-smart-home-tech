package ru.yandex.practicum.service.hub.device.removed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

// Определяет создание специфичного события и присваивает его в HubEventProto
@SpringBootTest
public class DeviceRemovedHandlerImplTest extends DeviceRemovedHandler {
    @Autowired
    public DeviceRemovedHandlerImplTest(HubEventHandleFactory hubEventHandleFactory,
                                        KafkaProducerConfig kafkaProducerConfig,
                                        HubEventAvroMapper hubEventAvroMapper,
                                        HubEventProtoMapper hubEventProtoMapper) {
        super(hubEventHandleFactory,
                kafkaProducerConfig,
                hubEventAvroMapper,
                hubEventProtoMapper);
    }

    @Override
    protected HubEventProto createSpecificHubProto() {
        // Значения для события
        String id = "1";

        DeviceRemovedEventProto deviceRemovedEventProto = DeviceRemovedEventProto.newBuilder()
                .setId(id)
                .build();
        sourceProto = deviceRemovedEventProto;

        return fillHubProtoBaseFields(HubEventProto.newBuilder())
                .setDeviceRemoved(deviceRemovedEventProto)
                .build();
    }
}
