package ru.yandex.practicum.service.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.EnumMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler(KafkaConfig kafkaConfig, KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate) {
        super(kafkaConfig, kafkaTemplate);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEventProto eventProto) {
        ScenarioAddedEventProto scenarioAddedEvent = eventProto.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(mapConditionsToAvro(scenarioAddedEvent.getConditionList()))
                .setActions(mapActionsToAvro(scenarioAddedEvent.getActionList()))
                .build();
    }

    private List<ScenarioConditionAvro> mapConditionsToAvro(List<ScenarioConditionProto> conditions) {
        List<ScenarioConditionAvro> listAvro = new ArrayList<>();
        for (ScenarioConditionProto condition : conditions) {
            ScenarioConditionAvro conditionAvro = ScenarioConditionAvro.newBuilder()
                    .setSensorId(condition.getSensorId())
                    .setType(EnumMapper.mapEnum(condition.getType(), ConditionTypeAvro.class))
                    .setOperation(EnumMapper.mapEnum(condition.getOperation(), ConditionOperationAvro.class))
                    .setValue(mapUnionValue(condition))
                    .build();
            listAvro.add(conditionAvro);
        }
        return listAvro;
    }

    private Object mapUnionValue(ScenarioConditionProto condition) {
        if (condition.hasBoolValue())
            return condition.getBoolValue();
        else if (condition.hasIntValue())
            return condition.getIntValue();
        else
            return null;
    }

    private List<DeviceActionAvro> mapActionsToAvro(List<DeviceActionProto> actions) {
        List<DeviceActionAvro> listAvro = new ArrayList<>();
        for (DeviceActionProto action : actions) {
            DeviceActionAvro actionAvro = DeviceActionAvro.newBuilder()
                    .setSensorId(action.getSensorId())
                    .setType(EnumMapper.mapEnum(action.getType(), ActionTypeAvro.class))
                    .setValue(action.getValue())
                    .build();
            listAvro.add(actionAvro);
        }
        return listAvro;
    }

}
