package ru.yandex.practicum.grpc.eventhandlers.sensors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("Got event {} for handling", event);
        SensorEventAvro climateSensorAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(),
                        event.getTimestamp().getNanos()))
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setTemperatureC(event.getClimateSensorEvent().getTemperatureC())
                        .setHumidity(event.getClimateSensorEvent().getHumidity())
                        .setCo2Level(event.getClimateSensorEvent().getCo2Level())
                        .build())
                .build();
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(sensorTopic, null,
                climateSensorAvro.getTimestamp().toEpochMilli(), climateSensorAvro.getHubId(),
                climateSensorAvro);
        kafkaProducer.send(record);
        log.info("Send to topic {} climate event {}", sensorTopic, climateSensorAvro);

    }
}
