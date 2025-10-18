package ru.yandex.practicum.grpc.eventhandlers.sensors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;


    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Got event {} for handling", event);
        SensorEventAvro motionSensorAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(MotionSensorAvro.newBuilder()
                        .setLinkQuality(event.getMotionSensorEvent().getLinkQuality())
                        .setMotion(event.getMotionSensorEvent().getMotion())
                        .setVoltage(event.getMotionSensorEvent().getVoltage())
                        .build())
                .build();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(sensorTopic, null,
                motionSensorAvro.getTimestamp().toEpochMilli(), motionSensorAvro.getHubId(),
                motionSensorAvro);
        kafkaProducer.send(record);
        log.info("Send to topic {} motion event {}", sensorTopic, motionSensorAvro);

    }
}
