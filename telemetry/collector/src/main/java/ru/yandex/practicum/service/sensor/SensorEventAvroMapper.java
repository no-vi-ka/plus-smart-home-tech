package ru.yandex.practicum.service.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorEventAvroMapper {
    ClimateSensorAvro mapToClimateSensorAvro(ClimateSensorEvent climateSensorEvent);

    LightSensorAvro mapToLightSensorAvro(LightSensorEvent lightSensorEvent);

    MotionSensorAvro mapToMotionSensorAvro(MotionSensorEvent motionSensorEvent);

    SwitchSensorAvro mapToSwitchSensorAvro(SwitchSensorEvent switchSensorEvent);

    TemperatureSensorAvro mapToTemperatureSensorAvro(TemperatureSensorEvent temperatureSensorEvent);
}
