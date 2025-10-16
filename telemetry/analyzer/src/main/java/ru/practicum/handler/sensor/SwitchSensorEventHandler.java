package ru.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Slf4j
@Component
public class SwitchSensorEventHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return SwitchSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro) {
        SwitchSensorAvro switchSensorAvro = (SwitchSensorAvro) sensorStateAvro.getData();
        return switch (type) {
            case ConditionType.SWITCH -> switchSensorAvro.getState() ? 1 : 0;
            default -> null;
        };
    }
}
