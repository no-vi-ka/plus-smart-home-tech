package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;
import ru.yandex.practicum.service.handler.BaseSensorHandler;

@Service
public class SwitchSensorHandler extends BaseSensorHandler<SwitchSensorAvro> {
    public SwitchSensorHandler(Config config, Producer producer) {
        super(config, producer);
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEventProto event) {
        var switchEvent = event.getSwitchSensorEvent();
        return new SwitchSensorAvro(switchEvent.getState());
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

}
