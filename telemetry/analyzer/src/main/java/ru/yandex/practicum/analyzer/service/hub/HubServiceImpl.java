package ru.yandex.practicum.analyzer.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.action.ActionRepository;
import ru.yandex.practicum.analyzer.action.model.Action;
import ru.yandex.practicum.analyzer.action.model.ActionType;
import ru.yandex.practicum.analyzer.condition.ConditionRepository;
import ru.yandex.practicum.analyzer.condition.model.Condition;
import ru.yandex.practicum.analyzer.condition.model.ConditionOperation;
import ru.yandex.practicum.analyzer.condition.model.ConditionType;
import ru.yandex.practicum.analyzer.exception.NotFoundException;
import ru.yandex.practicum.analyzer.scenario.ScenarioRepository;
import ru.yandex.practicum.analyzer.scenario.model.Scenario;
import ru.yandex.practicum.analyzer.scenario.model.ScenarioAction;
import ru.yandex.practicum.analyzer.scenario.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.sensor.SensorRepository;
import ru.yandex.practicum.analyzer.sensor.model.Sensor;
import ru.yandex.practicum.analyzer.util.ValueToInteger;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HubServiceImpl implements HubService {
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Transactional
    @Override
    public void addDevice(DeviceAddedEventAvro deviceAddedEventAvro, String hubId) {
        String deviceId = deviceAddedEventAvro.getId();
        log.info("start add device hubId={}, deviceId={}", hubId, deviceId);

        boolean alreadySaved = sensorRepository.findByIdAndHubId(deviceId, hubId).isPresent();
        // Игнорировать повторное добавление
        if (alreadySaved) {
            log.info("already saved device hubId={}, deviceId={}", hubId, deviceId);
            return;
        }

        Sensor sensor = Sensor.builder()
                .hubId(hubId)
                .id(deviceId)
                .build();

        sensorRepository.save(sensor);
        log.info("success add device hubId={}, deviceId={}", hubId, deviceId);
    }

    @Override
    public void removeDevice(DeviceRemovedEventAvro deviceRemovedEventAvro) {
        log.info("remove device id={}", deviceRemovedEventAvro.getId());
        sensorRepository.deleteById(deviceRemovedEventAvro.getId());
    }

    @Transactional
    @Override
    public void addScenario(ScenarioAddedEventAvro scenarioAddedEventAvro, String hubId) {
        String scenarioName = scenarioAddedEventAvro.getName();
        log.info("start add scenario hubId={}, scenarioName={}", hubId, scenarioName);
        Optional<Scenario> scenarioOptional = scenarioRepository.findByHubIdAndName(hubId, scenarioName);

        // Сценарий
        Scenario scenario;

        // Если уже есть - обновить запись
        if (scenarioOptional.isPresent()) {
            log.trace("Найден сценарий, происходит обновление hubId={}, scenarioName={}", hubId, scenarioName);
            scenario = scenarioOptional.get();

            // Удаление существующих данных для перезааписи (actions)
            log.trace("Удаление actions start");

            List<Long> actionsIds = scenario.getScenarioActions().stream()
                    .map(action -> action.getId().getActionId())
                    .toList();

            for (ScenarioAction scenarioAction : scenario.getScenarioActions()) {
                scenario.removeScenarioAction(scenarioAction);
            }

            actionRepository.deleteAllById(actionsIds);
            log.trace("Удаление actions end");

            // Удаление существующих данных для перезааписи (conditions)
            log.trace("Удаление conditions start");

            List<Long> conditionsIds = scenario.getScenarioConditions().stream()
                    .map(condition -> condition.getCondition().getId())
                    .toList();

            for (ScenarioCondition scenarioCondition : scenario.getScenarioConditions()) {
                scenario.removeScenarioCondition(scenarioCondition);
            }

            conditionRepository.deleteAllById(conditionsIds);
            log.trace("Удаление conditions end");
        } else {
            log.trace("Не найден сценарий, происходит добавление hubId={}, scenarioName={}", hubId, scenarioName);
            scenario = Scenario.builder()
                    .hubId(hubId)
                    .name(scenarioName)
                    .build();
            scenarioRepository.save(scenario);
        }

        // id сенсоров для последующей загрузки
        Set<String> sensorIds = new HashSet<>();
        scenarioAddedEventAvro.getConditions().forEach(conditionAvro -> sensorIds.add(conditionAvro.getSensorId()));
        scenarioAddedEventAvro.getActions().forEach(actionAvro -> sensorIds.add(actionAvro.getSensorId()));
        log.trace("Получены id сенсоров для загрузки из БД");

        // загрузка сенсоров за 1 запрос
        Map<String, Sensor> sensorsMap = sensorRepository.findAllById(sensorIds).stream()
                .collect(Collectors.toMap(Sensor::getId, sensor -> sensor));
        log.trace("Сенсоры загружены");

        // Проверям, все ли сенсоры найдены
        if (sensorsMap.size() != sensorIds.size()) {
            List<String> notFoundIds = new ArrayList<>();

            for (String sensorId : sensorIds) {
                if (!sensorsMap.containsKey(sensorId)) {
                    notFoundIds.add(sensorId);
                }
            }
            int countNotFound = sensorIds.size() - sensorsMap.size();

            String msg = String.format("Не найдено %d сенсоров, ids=%s", countNotFound, notFoundIds);
            log.warn(msg);
            throw new NotFoundException(msg);
        }
        log.trace("Кол-во сенсоров соответствует кол-ву id ");

        // Условия
        for (ScenarioConditionAvro conditionAvro : scenarioAddedEventAvro.getConditions()) {
            Condition condition = Condition.builder()
                    .type(ConditionType.valueOf(conditionAvro.getType().name()))
                    .operation(ConditionOperation.valueOf(conditionAvro.getOperation().name()))
                    .value(ValueToInteger.convert(conditionAvro.getValue()))
                    .build();

            condition = conditionRepository.save(condition);

            // Получаем сенсор из мапы
            Sensor sensor = sensorsMap.get(conditionAvro.getSensorId());

            // Создаем ScenarioCondition
            ScenarioCondition scenarioCondition = new ScenarioCondition(scenario, sensor, condition);
            scenario.addScenarioCondition(scenarioCondition);
        }
        log.trace("Успешное добавление условий");

        // Действия
        for (DeviceActionAvro actionAvro : scenarioAddedEventAvro.getActions()) {
            Action action = Action.builder()
                    .type(ActionType.valueOf(actionAvro.getType().name()))
                    .value(actionAvro.getValue())
                    .build();

            action = actionRepository.save(action);

            // Получаем сенсор из мапы
            Sensor sensor = sensorsMap.get(actionAvro.getSensorId());

            // Создаем ScenarioAction
            ScenarioAction scenarioAction = new ScenarioAction(scenario, sensor, action);
            scenario.addScenarioAction(scenarioAction);
        }
        log.trace("Успешное добавление действий");

        log.info("success add scenario hubId={}, scenarioName={}", hubId, scenarioName);
    }

    @Transactional
    @Override
    public void removeScenario(ScenarioRemovedEventAvro scenarioRemovedEventAvro, String hubId) {
        String scenarioName = scenarioRemovedEventAvro.getName();
        log.info("start remove scenario, hubId={}, name={}", hubId, scenarioName);
        scenarioRepository.deleteByHubIdAndName(hubId, scenarioName);
    }
}
