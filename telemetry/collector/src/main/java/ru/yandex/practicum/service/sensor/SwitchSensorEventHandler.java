package ru.yandex.practicum.service.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {

    public SwitchSensorEventHandler(KafkaConfig kafkaConfig, KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate) {
        super(kafkaConfig, kafkaTemplate);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEventProto eventProto) {
        return SwitchSensorAvro.newBuilder()
                .setState(eventProto.getSwitchSensorEvent().getState())
                .build();
    }

}
