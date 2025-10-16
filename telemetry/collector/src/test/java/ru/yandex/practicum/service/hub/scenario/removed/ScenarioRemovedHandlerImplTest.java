package ru.yandex.practicum.service.hub.scenario.removed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

// Определяет создание специфичного события и присваивает его в HubEventProto
@SpringBootTest
public class ScenarioRemovedHandlerImplTest extends ScenarioRemovedHandler {
    @Autowired
    public ScenarioRemovedHandlerImplTest(HubEventHandleFactory hubEventHandleFactory,
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
        String scenarioName = "Test 1";

        ScenarioRemovedEventProto scenarioRemovedEventProto = ScenarioRemovedEventProto.newBuilder()
                .setName(scenarioName)
                .build();
        sourceProto = scenarioRemovedEventProto;

        return fillHubProtoBaseFields(HubEventProto.newBuilder())
                .setScenarioRemoved(scenarioRemovedEventProto)
                .build();
    }
}
