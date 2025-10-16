package ru.yandex.practicum.service.hub.device.added;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.service.hub.BaseHubEventHandlerTest;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определяет проверку полей специфичного события
abstract class DeviceAddedHandler extends BaseHubEventHandlerTest {
    protected DeviceAddedEventProto sourceProto;

    @Autowired
    public DeviceAddedHandler(HubEventHandleFactory hubEventHandleFactory,
                              KafkaProducerConfig kafkaProducerConfig,
                              HubEventAvroMapper hubEventAvroMapper,
                              HubEventProtoMapper hubEventProtoMapper) {
        super(hubEventHandleFactory,
                kafkaProducerConfig,
                hubEventAvroMapper,
                hubEventProtoMapper,
                hubEventHandleFactory.getHubEventHandlerByPayloadCase(HubEventProto.PayloadCase.DEVICE_ADDED));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного события
        DeviceAddedEventAvro targetAvro = (DeviceAddedEventAvro) targetBase.getPayload();
        assertEquals(sourceProto.getId(), targetAvro.getId());
        assertEquals(sourceProto.getType().name(), targetAvro.getType().name());
    }
}
