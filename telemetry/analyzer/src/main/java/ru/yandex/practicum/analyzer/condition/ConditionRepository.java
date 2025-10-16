package ru.yandex.practicum.analyzer.condition;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.condition.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}