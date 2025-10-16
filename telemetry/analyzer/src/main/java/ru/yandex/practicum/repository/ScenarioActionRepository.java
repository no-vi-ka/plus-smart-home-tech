package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.ScenarioActionId;

import java.util.List;
import java.util.Set;

public interface ScenarioActionRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {
    List<ScenarioAction> findByScenarioId(Long scenarioId);
    List<ScenarioAction> findBySensorId(String sensorId);
    @Query("SELECT sc.id.scenarioId FROM ScenarioCondition sc " +
            "WHERE sc.id.scenarioId IN :scenarioIds")
    Set<Long> findExistingScenarioIds(List<Long> scenarioIds);
}
