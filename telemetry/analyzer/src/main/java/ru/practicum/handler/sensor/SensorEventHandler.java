package ru.practicum.handler.sensor;

import ru.practicum.model.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;

public interface SensorEventHandler {
    String getType();

    Integer getSensorValue(ConditionType type, SensorStateAvro sensorStateAvro);
}
