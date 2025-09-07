package ru.yandex.practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.config.KafkaTopicsNames;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.service.mapper.sensor.SensorEventAvroMapper;
import ru.yandex.practicum.service.mapper.sensor.SensorEventProtoMapper;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorHandler implements SensorEventHandler {
    protected final KafkaEventProducer producer;
    protected final KafkaTopicsNames topicsNames;
    protected final SensorEventAvroMapper avroMapper;
    protected final SensorEventProtoMapper protoMapper;

    protected abstract SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent);

    protected abstract SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto);

    @Override
    public void handle(SensorEventProto sensorProto) {
        if (sensorProto == null) {
            throw new IllegalArgumentException("HubEvent cannot be null");
        }
        SensorEvent sensor = mapSensorProtoToModel(sensorProto);
        log.debug("map To SENSOR confirm hubId={}", sensor.getHubId());
        SensorEventAvro avro = mapSensorEventToAvro(sensor);
        log.debug("map To AVRO confirm hubId={}", sensor.getHubId());
        ProducerRecord<String, SpecificRecordBase> param = createProducerSendParam(sensor, avro);
        log.debug("param created confirm hubId={}", sensor.getHubId());
        producer.sendRecord(param);
        log.debug("record send confirm hubId={}", sensor.getHubId());
    }

    protected SensorEventAvro buildSensorEventAvro(SensorEvent sensorEvent, SpecificRecordBase payloadAvro) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }

    protected SensorEvent mapBaseSensorProtoFieldsToSensor(SensorEvent sensor, SensorEventProto sensorProto) {
        sensor.setId(sensorProto.getId());
        sensor.setHubId(sensorProto.getHubId());

        long seconds = sensorProto.getTimestamp().getSeconds();
        int nanos = sensorProto.getTimestamp().getNanos();

        sensor.setTimestamp(Instant.ofEpochSecond(seconds, nanos));
        return sensor;
    }

    private ProducerRecord<String, SpecificRecordBase> createProducerSendParam(SensorEvent event, SensorEventAvro avro) {
        return new ProducerRecord<>(topicsNames.getSensorsTopic(), null, event.getTimestamp().toEpochMilli(), event.getHubId(), avro);
    }
}