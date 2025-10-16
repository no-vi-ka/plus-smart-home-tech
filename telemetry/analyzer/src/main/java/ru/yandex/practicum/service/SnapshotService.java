package ru.yandex.practicum.service;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.evaluator.ScenarioEvaluator;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioEvaluator scenarioEvaluator;

    @Transactional
    public void processSnapshot(SensorsSnapshotAvro snapshot) {
        scenarioEvaluator.evaluateSnapshot(snapshot);
    }
}

