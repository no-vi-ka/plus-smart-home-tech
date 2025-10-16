package ru.practicum.event.mapper.proto;

import org.springframework.stereotype.Component;
import ru.practicum.event.model.sensor.ClimateSensorEvent;
import ru.practicum.event.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Component
public class ClimateSensorEventMapper implements SensorEventProtoMapper {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public SensorEvent map(SensorEventProto event) {
        ClimateSensorProto sensorEvent = event.getClimateSensorEvent();

        ClimateSensorEvent climateSensorEvent = ClimateSensorEvent.builder()
                .id(event.getId())
                .hubId(event.getHubId())
                .timestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .temperatureC(sensorEvent.getTemperatureC())
                .co2Level(sensorEvent.getCo2Level())
                .humidity(sensorEvent.getHumidity())
                .build();
        return climateSensorEvent;
    }
}
