package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.sensors}")
    private String sensorTopic;

    @Value("${topics.hubs}")
    private String hubTopic;

    @Override
    public SensorEvent addSensorEvent(SensorEvent sensorEvent) {
        sensorEvent.getType().addSensorEvent(sensorEvent, sensorTopic, kafkaProducer);
        return sensorEvent;
    }

    @Override
    public HubEvent addHubEvent(HubEvent hubEvent) {
        hubEvent.getType().addHubEvent(hubEvent, hubTopic, kafkaProducer);
        return hubEvent;
    }
}
