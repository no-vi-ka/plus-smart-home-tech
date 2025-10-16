package ru.practicum.event.mapper.sensor.mapstruct;

import org.mapstruct.Mapper;
import ru.practicum.event.model.SensorEvent;
import ru.practicum.event.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import java.time.Instant;

    @Mapper(componentModel = SPRING)
    public interface SensorJavaToAvroMapper {
    
        ClimateSensorAvro toAvro(ClimateSensorEvent event);
    
        LightSensorAvro toAvro(LightSensorEvent event);
    
        MotionSensorAvro toAvro(MotionSensorEvent event);
    
        SwitchSensorAvro toAvro(SwitchSensorEvent event);
    
        TemperatureSensorAvro toAvro(TemperatureSensorEvent event);
    
        default SensorEventAvro toAvro(SensorEvent event) {
            Instant ts = event.getTimestamp();
            return switch (event) {
                case ClimateSensorEvent e -> new SensorEventAvro(event.getId(), event.getHubId(), ts, toAvro(e));
                case LightSensorEvent e -> new SensorEventAvro(event.getId(), event.getHubId(), ts, toAvro(e));
                case MotionSensorEvent e -> new SensorEventAvro(event.getId(), event.getHubId(), ts, toAvro(e));
                case SwitchSensorEvent e -> new SensorEventAvro(event.getId(), event.getHubId(), ts, toAvro(e));
                case TemperatureSensorEvent e -> new SensorEventAvro(event.getId(), event.getHubId(), ts, toAvro(e));
                default -> throw new IllegalArgumentException("Unsupported SensorEvent type: " + event.getClass());
            };
        }
    }

