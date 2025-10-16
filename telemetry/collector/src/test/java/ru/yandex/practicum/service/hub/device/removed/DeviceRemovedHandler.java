package ru.yandex.practicum.service.hub.device.removed;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.service.hub.BaseHubEventHandlerTest;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определяет проверку полей специфичного события
public abstract class DeviceRemovedHandler extends BaseHubEventHandlerTest {
    protected DeviceRemovedEventProto sourceProto;

    @Autowired
    public DeviceRemovedHandler(HubEventHandleFactory hubEventHandleFactory,
                                KafkaProducerConfig kafkaProducerConfig,
                                HubEventAvroMapper hubEventAvroMapper,
                                HubEventProtoMapper hubEventProtoMapper) {
        super(hubEventHandleFactory,
                kafkaProducerConfig,
                hubEventAvroMapper,
                hubEventProtoMapper,
                hubEventHandleFactory.getHubEventHandlerByPayloadCase(HubEventProto.PayloadCase.DEVICE_REMOVED));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного события
        DeviceRemovedEventAvro targetAvro = (DeviceRemovedEventAvro) targetBase.getPayload();
        assertEquals(sourceProto.getId(), targetAvro.getId());
    }
}
