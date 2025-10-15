package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.HubEventHandler;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro hubEvent) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) hubEvent.getPayload();
        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(hubEvent.getHubId(), scenarioRemovedEvent.getName());

        if (scenarioOpt.isPresent()) {
            Scenario scenario = scenarioOpt.get();
            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
        } else {
            log.info("Сценарий не найден.");
        }
    }

    @Override
    public String getMessageType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }

}
