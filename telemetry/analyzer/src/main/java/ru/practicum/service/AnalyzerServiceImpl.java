package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.handler.sensor.SensorEventHandler;
import ru.practicum.model.Condition;
import ru.practicum.model.ConditionOperation;
import ru.practicum.model.Scenario;
import ru.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyzerServiceImpl implements AnalyzerService {
    private final ScenarioRepository scenarioRepository;
    private final Map<String, SensorEventHandler> sensorEventHandlers ;

    public AnalyzerServiceImpl(ScenarioRepository scenarioRepository,
                               Set<SensorEventHandler> sensorEventHandlers) {
        this.scenarioRepository = scenarioRepository;
        log.info("AnalyzerServiceImpl: scenarioRepository initialized");
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getType,
                        Function.identity()
                ));
        log.info("AnalyzerServiceImpl: sensorEventHandlers initialized");
    }

    @Override
    public List<Scenario> getScenariosBySnapshot(SensorsSnapshotAvro sensorsSnapshotAvro) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshotAvro.getHubId());
        Map<String, SensorStateAvro> sensorStates = sensorsSnapshotAvro.getSensorsState();
        log.info("scenarios in repository count {} ", scenarios.size());

        return scenarios.stream()
                .filter(scenario -> checkConditions(scenario.getConditions(), sensorStates))
                .toList();
    }

    private boolean checkConditions(List<Condition> conditions, Map<String, SensorStateAvro> sensorStates) {
        log.info("<<<< checkConditions: conditions {}", conditions.toString());
        return conditions.stream()
                .allMatch(condition -> checkCondition(condition, sensorStates.get(condition.getSensor().getId())));
    }

    private boolean checkCondition(Condition condition, SensorStateAvro sensorStateAvro) {
        String type = sensorStateAvro.getData().getClass().getName();
        if (!sensorEventHandlers.containsKey(type)) {
            throw new IllegalArgumentException("Не могу найти обработчик для сенсора " + type);
        }
        Integer value = sensorEventHandlers.get(type).getSensorValue(condition.getType(), sensorStateAvro);
        log.info("check condition {} for sensor state {} ", condition, sensorStateAvro);
        if (value == null) {
            return false;
        }
        log.info("condition value = {}, sensor value = {}", condition.getValue(), value);
        return switch (condition.getOperation()) {
            case ConditionOperation.LOWER_THAN -> value < condition.getValue();
            case ConditionOperation.EQUALS -> value.equals(condition.getValue());
            case ConditionOperation.GREATER_THAN -> value > condition.getValue();
        };
    }
}
