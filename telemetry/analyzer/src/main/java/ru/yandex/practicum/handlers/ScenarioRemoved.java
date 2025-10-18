package ru.yandex.practicum.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ScenarioRemoved implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    public String getMessageType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }

    @Override
    public void handle(HubEventAvro hubEvent) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) hubEvent.getPayload();
        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(hubEvent.getHubId(),
                scenarioRemovedEvent.getName()
        );

        if (scenarioOpt.isPresent()) {
            Scenario scenario = scenarioOpt.get();
            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
        }

    }
}
