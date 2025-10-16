package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.BaseSensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.kafka.KafkaEventProducer;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler {
    public TemperatureSensorEventHandler(KafkaEventProducer kafkaEventProducer,
                                         KafkaProducerConfig kafkaProducerConfig,
                                         SensorEventAvroMapper sensorEventAvroMapper,
                                         SensorEventProtoMapper sensorEventProtoMapper) {
        super(kafkaEventProducer, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    protected SensorEventAvro mapSensorToAvro(BaseSensorEvent event) {
        TemperatureSensorAvro avro = sensorEventAvroMapper.mapToTemperatureSensorAvro((TemperatureSensorEvent) event);
        return buildSensorEventAvro(event, avro);
    }

    @Override
    protected BaseSensorEvent mapProtoToSensor(SensorEventProto eventProto) {
        BaseSensorEvent event = sensorEventProtoMapper.mapToTemperatureSensor(eventProto.getTemperatureSensorEvent());
        return sensorEventProtoMapper.mapBaseFields(event, eventProto);
    }
}
