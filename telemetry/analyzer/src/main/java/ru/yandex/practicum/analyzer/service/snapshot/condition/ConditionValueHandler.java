package ru.yandex.practicum.analyzer.service.snapshot.condition;

import ru.yandex.practicum.analyzer.condition.model.ConditionType;

/**
 * Преобразует значение из Condition в Integer
 */
public interface ConditionValueHandler {
    ConditionType getType();

    Integer handleValue(final Object value);
}
