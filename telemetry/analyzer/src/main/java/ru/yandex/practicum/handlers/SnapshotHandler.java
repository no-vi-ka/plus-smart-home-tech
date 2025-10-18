package ru.yandex.practicum.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.client.HubRouterClient;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotHandler {

    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final HubRouterClient hubRouterClient;

    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        Map<String, SensorStateAvro> states = snapshot.getSensorsState();
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());
        log.info("Scenarios {}", scenarios);
        scenarios.stream()
                .filter(scenario -> handleScenario(scenario, states))
                .forEach(scenario -> {
                            sendScenarioActions(scenario);
                        }
                );
    }

    private Boolean handleScenario(Scenario scenario, Map<String, SensorStateAvro> states) {
        List<Condition> conditions = conditionRepository.findAllByScenario(scenario);
        return conditions.stream()
                .noneMatch(condition -> !handleCondition(condition, states));
    }

    private Boolean handleCondition(Condition condition, Map<String, SensorStateAvro> states) {
        String sensorId = condition.getSensor().getId();
        SensorStateAvro sensorState = states.get(sensorId);

        if (sensorState == null) {
            return false;
        }

        switch (condition.getType()) {
            case LUMINOSITY -> {
                LightSensorAvro lightSensor = (LightSensorAvro) sensorState.getData();
                return handleOperation(condition, lightSensor.getLuminosity());
            }

            case TEMPERATURE -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, climateSensor.getTemperatureC());
            }

            case MOTION -> {
                MotionSensorAvro motionSensor = (MotionSensorAvro) sensorState.getData();
                return handleOperation(condition, motionSensor.getMotion() ? 1 : 0);
            }

            case SWITCH -> {
                SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorState.getData();
                return handleOperation(condition, switchSensor.getState() ? 1 : 0);
            }

            case CO2LEVEL -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, climateSensor.getCo2Level());
            }

            case HUMIDITY -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return handleOperation(condition, climateSensor.getHumidity());
            }
            case null -> {
                return false;
            }
        }
    }

    private Boolean handleOperation(Condition condition, Integer curValue) {
        ConditionOperationAvro conditionOperation = condition.getOperation();
        Integer targetValue = condition.getValue();

        switch (conditionOperation) {
            case EQUALS -> {
                return targetValue == curValue;
            }
            case LOWER_THAN -> {
                return curValue < targetValue;
            }
            case GREATER_THAN -> {
                return curValue > targetValue;
            }
            case null -> {
                return null;
            }
        }
    }

    private void sendScenarioActions(Scenario scenario) {
        actionRepository.findAllByScenario(scenario).forEach(hubRouterClient::sendActionRequest);
        log.info("Sended reqeust");
    }

}
