package ru.yandex.practicum.service.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.BaseSensorEvent;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.kafka.KafkaEventProducer;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler {
    public ClimateSensorEventHandler(KafkaEventProducer kafkaEventProducer,
                                     KafkaProducerConfig kafkaProducerConfig,
                                     SensorEventAvroMapper sensorEventAvroMapper,
                                     SensorEventProtoMapper sensorEventProtoMapper) {
        super(kafkaEventProducer, kafkaProducerConfig, sensorEventAvroMapper, sensorEventProtoMapper);
    }

    @Override
    public SensorEventType getSensorEventType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    protected SensorEventAvro mapSensorToAvro(BaseSensorEvent event) {
        ClimateSensorAvro avro = sensorEventAvroMapper.mapToClimateSensorAvro((ClimateSensorEvent) event);
        return buildSensorEventAvro(event, avro);
    }

    @Override
    protected BaseSensorEvent mapProtoToSensor(SensorEventProto eventProto) {
        BaseSensorEvent event = sensorEventProtoMapper.mapToClimateSensor(eventProto.getClimateSensorEvent());
        return sensorEventProtoMapper.mapBaseFields(event, eventProto);
    }
}
