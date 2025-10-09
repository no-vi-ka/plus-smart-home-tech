package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseHubHandler;

@Service
public class ScenarioAddedEventHandler extends BaseHubHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        var scenarioEvent = event.getScenarioAdded();

        return new ScenarioAddedEventAvro(
                scenarioEvent.getName(),
                scenarioEvent.getConditionList().stream().map(this::mapConditionToAvro).toList(),
                scenarioEvent.getActionList().stream().map(this::mapActionToAvro).toList()
        );
    }

    private ScenarioConditionAvro mapConditionToAvro(ScenarioConditionProto condition) {
        return new ScenarioConditionAvro(
                condition.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.valueOf(condition.getType().name()),
                ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro.valueOf(condition.getOperation().name()),
                condition.getBoolValue()
        );
    }

    private DeviceActionAvro mapActionToAvro(DeviceActionProto action) {
        return new DeviceActionAvro(
                action.getSensorId(),
                ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro.valueOf(action.getType().name()),
                action.getValue()
        );
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
