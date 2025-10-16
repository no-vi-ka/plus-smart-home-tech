package ru.yandex.practicum.evaluator;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.client.HubRouterClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.model.Operation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioEvaluator {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final EvaluatorRegistry registry;
    private final HubRouterClient hubRouterClient;

    public void evaluateSnapshot(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());

        for (Scenario scenario : scenarios) {
            var scenarioConditions = scenarioConditionRepository.findByScenarioId(scenario.getId());

            boolean conditionsOk = scenarioConditions.stream()
                    .allMatch(sc -> {
                        var sensorId = sc.getSensor().getId();
                        var state = snapshot.getSensorsState().get(sensorId);
                        if (state == null) return false;

                        return registry.extractValue(
                                        ConditionType.valueOf(sc.getCondition().getType()),
                                        state
                                )
                                .map(val -> registry.compare(
                                        Operation.valueOf(sc.getCondition().getOperation()),
                                        val,
                                        BigDecimal.valueOf(sc.getCondition().getValue())
                                ))
                                .orElse(false);
                    });

            if (conditionsOk) {
                var scenarioActions = scenarioActionRepository.findByScenarioId(scenario.getId());

                scenarioActions.forEach(sa -> {
                    var act = sa.getAction();
                    var sensor = sa.getSensor();

                    var actionProto = registry.buildAction(
                            ActionType.valueOf(act.getType()),
                            sensor.getId(),
                            act.getValue()
                    );

                    var request = DeviceActionRequest.newBuilder()
                            .setHubId(scenario.getHubId())
                            .setScenarioName(scenario.getName())
                            .setAction(actionProto)
                            .setTimestamp(Timestamps.fromMillis(snapshot.getTimestamp().toEpochMilli()))
                            .build();

                    hubRouterClient.sendAction(request);
                });
            }
        }
    }
}
