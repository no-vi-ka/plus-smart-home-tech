package ru.yandex.practicum.service.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.model.sensor.BaseSensorEvent;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorEventProtoMapper {
    ClimateSensorEvent mapToClimateSensor(ClimateSensorProto proto);

    LightSensorEvent mapToLightSensor(LightSensorProto proto);

    MotionSensorEvent mapToMotionSensor(MotionSensorProto proto);

    SwitchSensorEvent mapToSwitchSensor(SwitchSensorProto proto);

    TemperatureSensorEvent mapToTemperatureSensor(TemperatureSensorProto proto);

    default BaseSensorEvent mapBaseFields(BaseSensorEvent event, SensorEventProto sensorEventProto) {
        event.setId(sensorEventProto.getId());
        event.setHubId(sensorEventProto.getHubId());

        long seconds = sensorEventProto.getTimestamp().getSeconds();
        int nanos = sensorEventProto.getTimestamp().getNanos();

        event.setTimestamp(Instant.ofEpochSecond(seconds, nanos));
        return event;
    }
}
