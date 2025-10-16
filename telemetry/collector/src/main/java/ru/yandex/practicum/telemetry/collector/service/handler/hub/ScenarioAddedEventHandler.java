package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEventProto eventProto) {
        ScenarioAddedEventProto payload = eventProto.getScenarioAdded();

        List<ScenarioConditionAvro> conditionsAvro = new ArrayList<>();
        if (!payload.getConditionList().isEmpty()) {
            conditionsAvro = payload.getConditionList().stream()
                    .map(this::mapConditionToAvro)
                    .toList();
        }

        List<DeviceActionAvro> actionsAvro = new ArrayList<>();
        if (!payload.getActionList().isEmpty()) {
            actionsAvro = payload.getActionList().stream()
                    .map(this::mapActionToAvro)
                    .toList();
        }

        return ScenarioAddedEventAvro.newBuilder()
                .setName(payload.getName())
                .setConditions(conditionsAvro)
                .setActions(actionsAvro)
                .build();

    }

    private ScenarioConditionAvro mapConditionToAvro(ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(condition.getBoolValue() ? true : condition.getIntValue())
                .build();
    }

    private DeviceActionAvro mapActionToAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }
}