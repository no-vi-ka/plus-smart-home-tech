package ru.yandex.practicum.service.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.BaseSensorEvent;
import ru.yandex.practicum.service.kafka.KafkaEventProducer;
import ru.yandex.practicum.service.SensorEventHandler;
import ru.yandex.practicum.service.kafka.ProducerSendParam;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler implements SensorEventHandler {

    protected final KafkaEventProducer kafkaEventProducer;
    protected final KafkaProducerConfig kafkaProducerConfig;
    protected final SensorEventAvroMapper sensorEventAvroMapper;
    protected final SensorEventProtoMapper sensorEventProtoMapper;

    protected abstract SensorEventAvro mapSensorToAvro(BaseSensorEvent event);

    protected abstract BaseSensorEvent mapProtoToSensor(SensorEventProto eventProto);

    @Override
    public void handle(SensorEventProto eventProto) {
        BaseSensorEvent event = mapProtoToSensor(eventProto);
        log.trace("map to event confirm hubId={}", event.getHubId());
        SensorEventAvro avro = mapSensorToAvro(event);
        log.trace("map to avro confirm hubId={}", event.getHubId());
        ProducerSendParam param = createProducerSendParam(event, avro);
        log.trace("param created confirm hubId={}", event.getHubId());
        kafkaEventProducer.send(param);
        log.trace("record send confirm hubId={}", event.getHubId());
    }

    protected ProducerSendParam createProducerSendParam(BaseSensorEvent event, SensorEventAvro avro) {
        return ProducerSendParam.builder()
                .topic(kafkaProducerConfig.getSensorsTopic())
                .timestamp(event.getTimestamp().toEpochMilli())
                .key(event.getHubId())
                .value(avro)
                .build();
    }

    protected SensorEventAvro buildSensorEventAvro(BaseSensorEvent event, SpecificRecordBase payloadAvro) {
        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }
}
