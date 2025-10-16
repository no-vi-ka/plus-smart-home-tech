package ru.yandex.practicum.analyzer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.condition.model.ConditionType;
import ru.yandex.practicum.analyzer.exception.NotFoundException;
import ru.yandex.practicum.analyzer.service.snapshot.condition.ConditionValueHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class ConditionValueHandlerFactory {
    private final Map<ConditionType, ConditionValueHandler> handlers;

    public ConditionValueHandlerFactory(Set<ConditionValueHandler> handlers) {
        this.handlers = new HashMap<>();
        handlers.forEach(handler -> this.handlers.put(handler.getType(), handler));
    }

    public ConditionValueHandler getHandlerByConditionType(ConditionType conditionType) {
        return Optional.ofNullable(handlers.get(conditionType))
                .orElseThrow(() -> {
                            String msg = conditionType + " not exist in map";
                            log.warn(msg);
                            return new NotFoundException(msg);
                        }
                );
    }
}
