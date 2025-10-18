package ru.yandex.practicum.grpc.eventhandlers.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.hubs}")
    private String hubTopic;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().getSeconds(),
                        hubEvent.getTimestamp().getNanos()))
                .setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(hubEvent.getScenarioAdded().getName())
                        .setConditions(mapToConditionAvro(hubEvent.getScenarioAdded().getConditionsList()))
                        .setActions(mapToDeviceActionAvro(hubEvent.getScenarioAdded().getActionsList()))
                        .build()
                )
                .build();

        kafkaProducer.send(new ProducerRecord<>(hubTopic, hubEventAvro));

    }

    private List<ScenarioConditionAvro> mapToConditionAvro(List<ScenarioConditionProto> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(
                                switch (condition.getType()) {
                                    case MOTION -> ConditionTypeAvro.MOTION;
                                    case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
                                    case SWITCH -> ConditionTypeAvro.SWITCH;
                                    case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
                                    case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
                                    case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
                                    case UNRECOGNIZED -> null;
                                }
                        )
                        .setOperation(
                                switch (condition.getOperation()) {
                                    case EQUALS -> ConditionOperationAvro.EQUALS;
                                    case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
                                    case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
                                    case UNRECOGNIZED -> null;
                                }
                        )
                        .setValue(
                                switch (condition.getValueCase()) {
                                    case INT_VALUE -> condition.getIntValue();
                                    case BOOL_VALUE -> condition.getBoolValue();
                                    case VALUE_NOT_SET -> null;
                                }
                        )
                        .build()
                )
                .toList();
    }

    private List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(
                                switch (action.getType()) {
                                    case ACTIVATE -> ActionTypeAvro.ACTIVATE;
                                    case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
                                    case INVERSE -> ActionTypeAvro.INVERSE;
                                    case SET_VALUE -> ActionTypeAvro.SET_VALUE;
                                    case UNRECOGNIZED -> null;
                                }
                        )
                        .setValue(action.getValue())
                        .build()
                )
                .toList();
    }
}
