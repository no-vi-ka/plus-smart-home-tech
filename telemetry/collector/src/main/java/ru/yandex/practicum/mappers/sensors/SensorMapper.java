package ru.yandex.practicum.mappers.sensors;

import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.sensor.*;


public class SensorMapper {
    public static ClimateSensorAvro mapToClimateSensorAvro(ClimateSensorEvent event) {
        return ClimateSensorAvro.newBuilder()
                .setHumidity(event.getHumidity())
                .setCo2Level(event.getCo2Level())
                .setTemperatureC(event.getTemperatureC())
                .build();
    }

    public static LightSensorAvro mapToLightSensorAvro(LightSensorEvent event) {
        return LightSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setLuminosity(event.getLuminosity())
                .build();
    }


    public static MotionSensorAvro mapToMotionSensorAvro(MotionSensorEvent event) {
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(event.getLinkQuality())
                .setMotion(event.getMotion())
                .setVoltage(event.getVoltage())
                .build();
    }

    public static SwitchSensorAvro mapToSwitchSensorAvro(SwitchSensorEvent event) {
        return SwitchSensorAvro.newBuilder()
                .setState(event.getState())
                .build();
    }

    public static TemperatureSensorAvro mapToTemperatureSensorAvro(TemperatureSensorEvent event) {
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(event.getTemperatureC())
                .setTemperatureF(event.getTemperatureF())
                .build();
    }
}
