package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.ScenarioConditionId;

import java.util.List;
import java.util.Set;

public interface ScenarioConditionRepository extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

    List<ScenarioCondition> findByScenarioId(Long scenarioId);

    List<ScenarioCondition> findBySensorId(String id);

    @Query("SELECT sc.id.scenarioId FROM ScenarioCondition sc " +
            "WHERE sc.id.scenarioId IN :scenarioIds")
    Set<Long> findExistingScenarioIds(List<Long> scenarioIds);
}

