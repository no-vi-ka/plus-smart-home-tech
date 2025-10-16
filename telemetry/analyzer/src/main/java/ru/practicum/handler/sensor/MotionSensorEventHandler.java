package ru.practicum.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

@Slf4j
@Component
public class MotionSensorEventHandler implements SensorEventHandler {
    @Override
    public String getType() {
        return MotionSensorAvro.class.getName();
    }

    @Override
    public Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro) {
        MotionSensorAvro motionSensorAvro = (MotionSensorAvro) sensorStateAvro.getData();
        return switch (type) {
            case ConditionType.MOTION -> motionSensorAvro.getMotion() ? 1 : 0;
            default -> null;
        };
    }
}
