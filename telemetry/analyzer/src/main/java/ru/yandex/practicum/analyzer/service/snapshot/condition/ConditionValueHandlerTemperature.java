package ru.yandex.practicum.analyzer.service.snapshot.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.condition.model.ConditionType;
import ru.yandex.practicum.analyzer.exception.NotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Slf4j
@Component
public class ConditionValueHandlerTemperature implements ConditionValueHandler {
    @Override
    public ConditionType getType() {
        return ConditionType.TEMPERATURE;
    }

    @Override
    public Integer handleValue(final Object value) {
        return switch (value) {
            case ClimateSensorAvro climateSensorAvro -> climateSensorAvro.getTemperatureC();
            case TemperatureSensorAvro temperatureSensorAvro -> temperatureSensorAvro.getTemperatureC();
            default -> {
                String msg = "Не найден сенсор " + getType() + " для " + value.getClass().getName();
                log.error(msg);
                throw new NotFoundException(msg);
            }
        };
    }
}
