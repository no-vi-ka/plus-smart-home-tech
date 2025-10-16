package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    public void handleEvent(HubEventAvro event) {
        var hubId = event.getHubId();
        var payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            handleDeviceAdded(hubId, deviceAdded);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            handleDeviceRemoved(hubId, deviceRemoved);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            handleScenarioAdded(hubId, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            handleScenarioRemoved(hubId, scenarioRemoved);
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro device) {
        var sensor = sensorRepository.findById(device.getId())
                .orElseGet(() -> {
                    var newSensor = new Sensor();
                    newSensor.setId(device.getId());
                    newSensor.setHubId(hubId);
                    return sensorRepository.save(newSensor);
                });
        if (!hubId.equals(sensor.getHubId())) {
            var oldConditions = scenarioConditionRepository.findBySensorId(device.getId());
            scenarioConditionRepository.deleteAll(oldConditions);

            var oldActions = scenarioActionRepository.findBySensorId(device.getId());
            scenarioActionRepository.deleteAll(oldActions);
            deleteOrphanedScenarios(hubId);
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro device) {
        sensorRepository.findByIdAndHubId(device.getId(), hubId)
                .ifPresent(sensor -> {
                    var conditions = scenarioConditionRepository.findBySensorId(device.getId());
                    scenarioConditionRepository.deleteAll(conditions);

                    var actions = scenarioActionRepository.findBySensorId(device.getId());
                    scenarioActionRepository.deleteAll(actions);

                    deleteOrphanedScenarios(hubId);

                    sensorRepository.delete(sensor);
                });
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        var scenario = scenarioRepository.findByHubIdAndName(hubId, scenarioAdded.getName())
                .orElseGet(() -> {
                    var newScenario = new Scenario();
                    newScenario.setHubId(hubId);
                    newScenario.setName(scenarioAdded.getName());
                    return newScenario;
                });
        scenario = scenarioRepository.save(scenario);

        var oldConditions = scenarioConditionRepository.findByScenarioId(scenario.getId());
        scenarioConditionRepository.deleteAll(oldConditions);

        var oldActions = scenarioActionRepository.findByScenarioId(scenario.getId());
        scenarioActionRepository.deleteAll(oldActions);

        for (ScenarioConditionAvro condAvro : scenarioAdded.getConditions()) {
            var cond = new Condition();
            cond.setType(condAvro.getType().name());
            cond.setOperation(condAvro.getOperation().name());
            cond.setValue(condAvro.getValue() != null ? (Integer) condAvro.getValue() : null);
            cond = conditionRepository.save(cond);
            var sensor = sensorRepository.findByIdAndHubId(condAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Sensor not found for condition"));
            var sc = new ScenarioCondition(new ScenarioConditionId(scenario.getId(), sensor.getId(), cond.getId()),
                    scenario, sensor, cond);
            scenarioConditionRepository.save(sc);
        }

        for (DeviceActionAvro actAvro : scenarioAdded.getActions()) {
            var act = new Action();
            act.setType(actAvro.getType().name());
            act.setValue(actAvro.getValue() != null ? (Integer) actAvro.getValue() : null);
            act = actionRepository.save(act);
            var sensor = sensorRepository.findByIdAndHubId(actAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Sensor not found for action"));
            var sa = new ScenarioAction(new ScenarioActionId(scenario.getId(), sensor.getId(), act.getId()),
                    scenario, sensor, act);
            scenarioActionRepository.save(sa);
        }
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro scenarioRemoved) {
        scenarioRepository.findByHubIdAndName(hubId, scenarioRemoved.getName())
                .ifPresent(scenario -> {
                    var conditions = scenarioConditionRepository.findByScenarioId(scenario.getId());
                    scenarioConditionRepository.deleteAll(conditions);
                    var actions = scenarioActionRepository.findByScenarioId(scenario.getId());
                    scenarioActionRepository.deleteAll(actions);
                    scenarioRepository.delete(scenario);
                });
    }

    private void deleteOrphanedScenarios(String hubId) {
        var scenarios = scenarioRepository.findByHubId(hubId);
        if (scenarios.isEmpty()) {
            return;
        }

        var scenarioIds = scenarios.stream().map(Scenario::getId).collect(Collectors.toList());

        var scenariosWithConditions = scenarioConditionRepository.findExistingScenarioIds(scenarioIds);

        var scenariosWithActions = scenarioActionRepository.findExistingScenarioIds(scenarioIds);

        var orphanedScenarios = scenarios.stream()
                .filter(scenario -> !scenariosWithConditions.contains(scenario.getId()) &&
                        !scenariosWithActions.contains(scenario.getId()))
                .collect(Collectors.toList());

        if (!orphanedScenarios.isEmpty()) {
            scenarioRepository.deleteAll(orphanedScenarios);
            log.debug("Deleted {} orphaned scenarios for hub: {}", orphanedScenarios.size(), hubId);
        }
    }
}

