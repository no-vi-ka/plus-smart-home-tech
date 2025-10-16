package ru.yandex.practicum.service.hub.scenario.added;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.service.hub.BaseHubEventHandlerTest;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

// Определяет проверку полей специфичного события
public abstract class ScenarioAddedHandler extends BaseHubEventHandlerTest {
    protected ScenarioAddedEventProto sourceProto;

    @Autowired
    public ScenarioAddedHandler(HubEventHandleFactory hubEventHandleFactory,
                                KafkaProducerConfig kafkaProducerConfig,
                                HubEventAvroMapper hubEventAvroMapper,
                                HubEventProtoMapper hubEventProtoMapper) {
        super(hubEventHandleFactory,
                kafkaProducerConfig,
                hubEventAvroMapper,
                hubEventProtoMapper,
                hubEventHandleFactory.getHubEventHandlerByPayloadCase(HubEventProto.PayloadCase.SCENARIO_ADDED));
    }

    @Override
    protected void checkSpecificAvroFields() {
        // Проверка полей специфичного события

        ScenarioAddedEventAvro targetAvro = (ScenarioAddedEventAvro) targetBase.getPayload();

        // Название
        assertEquals(sourceProto.getName(), targetAvro.getName());

        // Условия сценария
        List<ScenarioConditionProto> conditionExpect = sourceProto.getConditionsList();
        List<ScenarioConditionAvro> conditionTarget = targetAvro.getConditions();
        assertEquals(conditionExpect.size(), conditionTarget.size());

        boolean found = false;
        for (ScenarioConditionAvro target : conditionTarget) {
            for (ScenarioConditionProto expect : conditionExpect) {
                if (target.getSensorId().equals(expect.getSensorId())) {
                    found = true;
                    assertEquals(expect.getSensorId(), target.getSensorId());
                    assertEquals(expect.getOperation().name(), target.getOperation().name());
                    assertEquals(expect.getType().name(), target.getType().name());

                    if (expect.hasBoolValue()) {
                        assertEquals(expect.getBoolValue(), target.getValue());
                    } else if (expect.hasIntValue()) {
                        assertEquals(expect.getIntValue(), target.getValue());
                    }
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Not found target.sensorId == source.sensorId");
            }
        }

        // Действия сценария
        List<DeviceActionProto> actionExpect = sourceProto.getActionsList();
        List<DeviceActionAvro> actionTarget = targetAvro.getActions();
        assertEquals(actionExpect.size(), actionTarget.size());

        for (DeviceActionAvro target : actionTarget) {
            for (DeviceActionProto expect : actionExpect) {
                if (target.getSensorId().equals(expect.getSensorId())) {
                    found = true;
                    assertEquals(expect.getSensorId(), target.getSensorId());
                    assertEquals(expect.getType().name(), target.getType().name());

                    if (expect.hasValue()) {
                        assertEquals(expect.getValue(), target.getValue());
                    }
                }
                if (!found) {
                    throw new IllegalArgumentException("Not found target.sensorId == source.sensorId");
                }
            }
        }
    }
}
