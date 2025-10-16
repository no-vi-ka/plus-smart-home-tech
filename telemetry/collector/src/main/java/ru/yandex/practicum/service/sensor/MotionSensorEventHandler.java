package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.BaseSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.kafka.KafkaEventProducer;

@Component
public class MotionSensorEventHandler extends BaseSensorEventHandler {
    public MotionSensorEventHandler(KafkaEventProducer kafkaEventProducer,
                                    KafkaProducerConfig kafkaProducerConfig,
                                    SensorEventAvroMapper sensorEventAvroMapper,
                                    SensorEventProtoMapper sensorEventProtoMapper) {
        super(kafkaEventProducer, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    protected SensorEventAvro mapSensorToAvro(BaseSensorEvent event) {
        MotionSensorAvro avro = sensorEventAvroMapper.mapToMotionSensorAvro((MotionSensorEvent) event);
        return buildSensorEventAvro(event, avro);
    }

    @Override
    protected BaseSensorEvent mapProtoToSensor(SensorEventProto eventProto) {
        BaseSensorEvent event = sensorEventProtoMapper.mapToMotionSensor(eventProto.getMotionSensorEvent());
        return sensorEventProtoMapper.mapBaseFields(event, eventProto);
    }
}
