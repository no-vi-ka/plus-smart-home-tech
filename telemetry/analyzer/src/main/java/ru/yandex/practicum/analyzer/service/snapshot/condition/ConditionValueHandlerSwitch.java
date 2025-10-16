package ru.yandex.practicum.analyzer.service.snapshot.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.condition.model.ConditionType;
import ru.yandex.practicum.analyzer.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Slf4j
@Component
public class ConditionValueHandlerSwitch implements ConditionValueHandler {
    @Override
    public ConditionType getType() {
        return ConditionType.SWITCH;
    }

    @Override
    public Integer handleValue(final Object value) {
        if (value instanceof SwitchSensorAvro switchSensorAvro) {
            return switchSensorAvro.getState() ? 1 : 0;
        }
        String msg = "Не найден сенсор " + getType() + " для " + value.getClass().getName();
        log.error(msg);
        throw new NotFoundException(msg);
    }
}