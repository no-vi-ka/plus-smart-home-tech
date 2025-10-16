package ru.practicum.event.mapper.proto;

import org.springframework.stereotype.Component;
import ru.practicum.event.model.sensor.SensorEvent;
import ru.practicum.event.model.sensor.SwitchSensorEvent;
import ru.practicum.event.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;

import java.time.Instant;

@Component
public class TemperatureSensorEventMapper implements SensorEventProtoMapper {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        TemperatureSensorProto sensorEvent = event.getTemperatureSensorEvent();

        TemperatureSensorEvent temperatureSensorEvent = TemperatureSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .temperatureC(sensorEvent.getTemperatureC())
                .temperatureF(sensorEvent.getTemperatureF())
                .build();

        System.out.println("temperatureSensorEvent = " + temperatureSensorEvent);
        return temperatureSensorEvent;
    }
}
