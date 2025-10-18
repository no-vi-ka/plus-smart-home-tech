package ru.yandex.practicum.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScenarioAdded implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final ConditionRepository conditionRepository;

    @Override
    public String getMessageType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) hubEvent.getPayload();
        Scenario scenario = Scenario.builder()
                .name(scenarioAddedEvent.getName())
                .hubId(hubEvent.getHubId())
                .build();
        Scenario scenarioSaved = scenarioRepository.findByHubIdAndName(hubEvent.getHubId(),
                scenarioAddedEvent.getName()).orElseGet(() -> scenarioRepository.save(scenario));

        if (checkSensorInConditions(scenarioAddedEvent, hubEvent.getHubId())) {
            Set<Condition> conditions = scenarioAddedEvent.getConditions().stream()
                    .map(c -> Condition.builder()
                            .sensor(sensorRepository.findById(c.getSensorId()).orElseThrow())
                            .scenario(scenario)
                            .type(c.getType())
                            .operation(c.getOperation())
                            .value(setValue(c.getValue()))
                            .build())
                    .collect(Collectors.toSet());
            conditionRepository.saveAll(conditions);
        }
        if (checkSensorInActions(scenarioAddedEvent, hubEvent.getHubId())) {
            Set<Action> actions = scenarioAddedEvent.getActions().stream()
                    .map(action -> Action.builder()
                            .sensor(sensorRepository.findById(action.getSensorId()).orElseThrow())
                            .scenario(scenario)
                            .type(action.getType())
                            .value(action.getValue())
                            .build())
                    .collect(Collectors.toSet());
            actionRepository.saveAll(actions);
        }
    }

    private Boolean checkSensorInConditions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEvent.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList(), hubId
        );
    }

    private Boolean checkSensorInActions(ScenarioAddedEventAvro scenarioAddedEventAvro, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEventAvro.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList(), hubId
        );
    }

    private Integer setValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return (Boolean) value ? 1 : 0;
        }
    }
}
