package ru.yandex.practicum.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class ScenarioAddedEventHandler extends HubProtoHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    public HubEventAvro mapToAvro(HubEventProto eventProto) {
        ScenarioAddedEventProto scenarioAddedEventProto = eventProto.getScenarioAdded();
        log.info("==> scenarioAddedEventProto = {}", scenarioAddedEventProto);

        List<ScenarioConditionAvro> scenarioConditionAvroList = scenarioAddedEventProto.getConditionList()
                .stream().map(this::mapToScenarioConditionAvro).toList();
        log.info("map to scenarioConditionAvroList = {}", scenarioConditionAvroList);

        List<DeviceActionAvro> deviceActionAvroList = scenarioAddedEventProto.getActionList()
                .stream().map(this::mapToDeviceActionAvro).toList();
        log.info("map to deviceActionAvroList = {}", deviceActionAvroList);

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEventProto.getName())
                .setConditions(scenarioConditionAvroList)
                .setActions(deviceActionAvroList)
                .build();
        log.info("<== scenarioAddedEventAvro = {}", scenarioAddedEventAvro);

        return HubEventAvro.newBuilder()
                .setHubId(eventProto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(eventProto.getTimestamp().getSeconds(),
                        eventProto.getTimestamp().getNanos()))
                .setPayload(scenarioAddedEventAvro)
                .build();
    }

    private ScenarioConditionAvro mapToScenarioConditionAvro(ScenarioConditionProto scenarioConditionProto) {
        Object value;
        switch (scenarioConditionProto.getValueCase()) {
            case ScenarioConditionProto.ValueCase.BOOL_VALUE:
                value = scenarioConditionProto.getBoolValue();
                break;
            case ScenarioConditionProto.ValueCase.INT_VALUE:
                value = scenarioConditionProto.getIntValue();
                break;
            default:
                value = null;
        }

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioConditionProto.getSensorId())
                .setType(ConditionTypeAvro.valueOf(scenarioConditionProto.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(scenarioConditionProto.getOperation().name()))
                .setValue(value)
                .build();
    }

    private DeviceActionAvro mapToDeviceActionAvro(DeviceActionProto deviceActionProto) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceActionProto.getSensorId())
                .setType(DeviceActionTypeAvro.valueOf(deviceActionProto.getType().name()))
                .setValue(deviceActionProto.getValue())
                .build();
    }
}