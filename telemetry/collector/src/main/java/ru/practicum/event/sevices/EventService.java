package ru.practicum.event.sevices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.event.exceptions.KafkaSendException;
import ru.practicum.event.mapper.hub.mapstruct.HubJavaToAvroMapper;
import ru.practicum.event.mapper.sensor.mapstruct.SensorJavaToAvroMapper;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final SensorJavaToAvroMapper sensorJavaToAvroMapper;
    private final HubJavaToAvroMapper hubJavaToAvroMapper;
    private final Producer<String, SpecificRecordBase> kafkaProducer;

    private static final String HUB_TOPIC = "telemetry.hubs.v1";
    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";


    public void processSensor(SensorEvent sensorEvent) {
        SensorEventAvro avro = sensorJavaToAvroMapper.toAvro(sensorEvent);
        long timestamp = sensorEvent.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                SENSOR_TOPIC,
                null,
                timestamp,
                sensorEvent.getHubId(),
                avro);

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправления sensor event в Kafka. Id: {}, Error: {}",
                        sensorEvent.getId(), exception.getMessage(), exception);
                throw new KafkaSendException("Отправка sensor event", exception);
            } else {
                log.info("Успешная отправка sensor event. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

    public void processHub(HubEvent hubEvent) {
        HubEventAvro avro = hubJavaToAvroMapper.toAvro(hubEvent);
        long timestamp = hubEvent.getTimestamp().toEpochMilli();

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                HUB_TOPIC,
                null,
                timestamp,
                hubEvent.getHubId(),
                avro
        );

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Ошибка отправления hub event в Kafka. HubId: {}, Error: {}",
                        hubEvent.getHubId(), exception.getMessage(), exception);
                throw new KafkaSendException("Отправка hub event", exception);
            } else {
                log.info("Успешная отправка hub event. Topic: {}, Partition: {}, Offset: {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }

}


