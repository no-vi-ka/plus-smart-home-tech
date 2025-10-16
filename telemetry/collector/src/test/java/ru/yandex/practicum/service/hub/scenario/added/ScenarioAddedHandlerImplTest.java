package ru.yandex.practicum.service.hub.scenario.added;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.service.hub.HubEventAvroMapper;
import ru.yandex.practicum.service.hub.HubEventProtoMapper;
import ru.yandex.practicum.util.HubEventHandleFactory;

// Определяет создание специфичного события и присваивает его в HubEventProto
@SpringBootTest
public class ScenarioAddedHandlerImplTest extends ScenarioAddedHandler {
    @Autowired
    public ScenarioAddedHandlerImplTest(HubEventHandleFactory hubEventHandleFactory,
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
        // Условия для сценария
        ScenarioConditionProto condition1 = ScenarioConditionProto.newBuilder()
                .setSensorId("sensor-motion-1")
                .setType(ConditionTypeProto.MOTION)
                .setOperation(ConditionOperationProto.EQUALS)
                .setBoolValue(true)
                .build();

        ScenarioConditionProto condition2 = ScenarioConditionProto.newBuilder()
                .setSensorId("sensor-temperature-2")
                .setType(ConditionTypeProto.TEMPERATURE)
                .setOperation(ConditionOperationProto.GREATER_THAN)
                .setIntValue(25)
                .build();

        // Действия для сценария
        DeviceActionProto action1 = DeviceActionProto.newBuilder()
                .setSensorId("device-bulb-3")
                .setType(ActionTypeProto.ACTIVATE)
                .build();

        DeviceActionProto action2 = DeviceActionProto.newBuilder()
                .setSensorId("device-conditioner-1")
                .setType(ActionTypeProto.SET_VALUE)
                .setValue(75)
                .build();

        // Сценарий
        String scenarioName = "Test 1"; // Название сценария

        ScenarioAddedEventProto scenarioAddedEventProto = ScenarioAddedEventProto.newBuilder()
                .setName(scenarioName)
                .addConditions(condition1)
                .addConditions(condition2)
                .addActions(action1)
                .addActions(action2)
                .build();
        sourceProto = scenarioAddedEventProto;

        return fillHubProtoBaseFields(HubEventProto.newBuilder())
                .setScenarioAdded(scenarioAddedEventProto)
                .build();
    }
}
