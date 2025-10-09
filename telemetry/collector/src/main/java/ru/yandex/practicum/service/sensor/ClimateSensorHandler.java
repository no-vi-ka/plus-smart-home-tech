package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseSensorHandler;

@Service
public class ClimateSensorHandler extends BaseSensorHandler<ClimateSensorAvro> {

    public ClimateSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected ClimateSensorAvro mapToAvro(SensorEventProto event) {
        var climateEvent = event.getClimateSensorEvent();
        return new ClimateSensorAvro(
                climateEvent.getTemperatureC(),
                climateEvent.getHumidity(),
                climateEvent.getCo2Level()
        );
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }
}

