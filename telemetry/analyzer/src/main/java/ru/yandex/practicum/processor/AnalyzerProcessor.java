package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.HubRouterClient;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.service.ScenarioService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzerProcessor {

    private final ScenarioService scenarioService;
    private final HubRouterClient hubRouterClient;

    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioService.getScenariosByHubId(hubId);

        scenarios.forEach(scenario -> {
            if (isScenarioTriggered(scenario, snapshot)) {
                executeActions(scenario.getActions(), hubId);
            }
        });
    }

    private boolean isScenarioTriggered(Scenario scenario, SensorsSnapshotAvro snapshot) {
        return scenario.getConditions().stream()
                .allMatch(condition -> checkCondition(condition, snapshot));
    }

    private boolean checkCondition(Condition condition, SensorsSnapshotAvro snapshot) {
        SensorStateAvro sensorState = snapshot.getSensorsState().get(condition.getSensorId());

        if (sensorState == null) {
            log.warn("No data available from sensor {}", condition.getSensorId());
            return false;
        }

        try {
            return switch (condition.getType()) {
                case TEMPERATURE -> evaluateCondition(getValueFromTemperatureSensor(sensorState), condition);
                case HUMIDITY -> evaluateCondition(getValueFromClimateSensor(sensorState, ClimateSensorAvro::getHumidity), condition);
                case CO2LEVEL -> evaluateCondition(getValueFromClimateSensor(sensorState, ClimateSensorAvro::getCo2Level), condition);
                case LUMINOSITY -> evaluateCondition(getValueFromLightSensor(sensorState), condition);
                case MOTION -> evaluateCondition(getValueFromMotionSensor(sensorState), condition);
                case SWITCH -> evaluateCondition(getValueFromSwitchSensor(sensorState), condition);
                default -> {
                    log.warn("Unknown sensor type: {}", condition.getType());
                    yield false;
                }
            };
        } catch (Exception e) {
            log.error("Error reading data for condition {}: {}", condition, e.getMessage(), e);
            return false;
        }
    }

    private Integer getValueFromTemperatureSensor(SensorStateAvro state) {
        return state.getData() instanceof TemperatureSensorAvro temp ? temp.getTemperatureC() : null;
    }

    private Integer getValueFromClimateSensor(SensorStateAvro state, java.util.function.Function<ClimateSensorAvro, Integer> getter) {
        return state.getData() instanceof ClimateSensorAvro climate ? getter.apply(climate) : null;
    }

    private Integer getValueFromLightSensor(SensorStateAvro state) {
        return state.getData() instanceof LightSensorAvro light ? light.getLuminosity() : null;
    }

    private Integer getValueFromMotionSensor(SensorStateAvro state) {
        return state.getData() instanceof MotionSensorAvro motion ? (motion.getMotion() ? 1 : 0) : null;
    }

    private Integer getValueFromSwitchSensor(SensorStateAvro state) {
        return state.getData() instanceof SwitchSensorAvro switchSensor ? (switchSensor.getState() ? 1 : 0) : null;
    }

    private boolean evaluateCondition(Integer sensorValue, Condition condition) {
        if (sensorValue == null) return false;
        return switch (condition.getOperation()) {
            case EQUALS -> sensorValue.equals(condition.getValue());
            case GREATER_THAN -> sensorValue > condition.getValue();
            case LOWER_THAN -> sensorValue < condition.getValue();
        };
    }

    private void executeActions(List<Action> actions, String hubId) {
        actions.forEach(action -> hubRouterClient.executeAction(action, hubId));
    }
}