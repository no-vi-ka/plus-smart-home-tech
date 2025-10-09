package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseSensorHandler;


@Service
public class TemperatureSensorHandler extends BaseSensorHandler<TemperatureSensorAvro> {

    public TemperatureSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected TemperatureSensorAvro mapToAvro(SensorEventProto event) {
        var tempEvent = event.getTemperatureSensorEvent();

        return new TemperatureSensorAvro(
                event.getId(),
                event.getHubId(),
                event.getTimestamp().getSeconds(),
                tempEvent.getTemperatureC(),
                tempEvent.getTemperatureF()
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

}
