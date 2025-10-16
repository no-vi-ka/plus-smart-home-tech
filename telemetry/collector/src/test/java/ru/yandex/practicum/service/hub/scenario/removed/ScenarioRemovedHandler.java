package ru.yandex.practicum.service.hub.scenario.removed;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.service.hub.BaseHubEventHandlerTest;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Определяет проверку полей специфичного события
public abstract class ScenarioRemovedHandler extends BaseHubEventHandlerTest {
    protected ScenarioRemovedEventProto sourceProto;

    @Autowired
    public ScenarioRemovedHandler(HubEventHandleFactory hubEventHandleFactory,
                                  KafkaProducerConfig kafkaProducerConfig,
                                  HubEventAvroMapper hubEventAvroMapper,
                                  HubEventProtoMapper hubEventProtoMapper) {
        super(hubEventHandleFactory,
                kafkaProducerConfig,
                hubEventAvroMapper,
                hubEventProtoMapper,
                hubEventHandleFactory.getHubEventHandlerByPayloadCase(HubEventProto.PayloadCase.SCENARIO_REMOVED));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного события
        ScenarioRemovedEventAvro targetAvro = (ScenarioRemovedEventAvro) targetBase.getPayload();
        assertEquals(sourceProto.getName(), targetAvro.getName());
    }
}
