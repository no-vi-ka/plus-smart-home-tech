package ru.yandex.practicum.service.hub.device.added;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

// Определяет создание специфичного события и присваивает его в HubEventProto
@SpringBootTest
public class DeviceAddedHandlerImplTest extends DeviceAddedHandler {
    @Autowired
    public DeviceAddedHandlerImplTest(HubEventHandleFactory hubEventHandleFactory,
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
        DeviceTypeProto typeProto = DeviceTypeProto.CLIMATE_SENSOR;

        DeviceAddedEventProto deviceAddedEventProto = DeviceAddedEventProto.newBuilder()
                .setId(id)
                .setType(typeProto)
                .build();
        sourceProto = deviceAddedEventProto;

        // Создание прото Event
        return fillHubProtoBaseFields(HubEventProto.newBuilder())
                .setDeviceAdded(deviceAddedEventProto)
                .build();
    }
}
