package ru.yandex.practicum.mapper.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Slf4j
@Component
public class TemperatureSensorEventMapper extends BaseSensorEventMapper<TemperatureSensorAvro> {
    @Override
    protected TemperatureSensorAvro mapToAvroPayload(SensorEventProto event) {
        TemperatureSensorEventProto sensorEvent = event.getTemperatureSensorEvent();
        log.info("Mapper bring event to {}, result: {}", TemperatureSensorEventProto.class.getSimpleName(), sensorEvent);
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(sensorEvent.getTemperatureC())
                .setTemperatureF(sensorEvent.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getSensorEventType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }
}
