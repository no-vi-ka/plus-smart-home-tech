package ru.yandex.practicum.model.types;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.mappers.hub.HubMapper;
import ru.yandex.practicum.model.hub.*;

@Slf4j
public enum HubEventType {
    DEVICE_ADDED {
        @Override
        public void addHubEvent(HubEvent hubEvent,
                                String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing DeviceAddedEvent: hubId = {}, timestamp = {}",
                    hubEvent.getHubId(), hubEvent.getTimestamp()
            );
            log.debug("DeviceAddedEvent = {}", hubEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    HubMapper.mapToDeviceAddedEventAvro((DeviceAddedEvent) hubEvent)));
        }
    },
    DEVICE_REMOVED {
        @Override
        public void addHubEvent(HubEvent hubEvent,
                                String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing DeviceRemovedEvent: hubId = {}, timestamp = {}",
                    hubEvent.getHubId(), hubEvent.getTimestamp()
            );
            log.debug("DeviceAddedEvent = {}", hubEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    HubMapper.mapToDeviceRemovedEventAvro((DeviceRemovedEvent) hubEvent)));
        }
    },
    SCENARIO_ADDED {
        @Override
        public void addHubEvent(HubEvent hubEvent,
                                String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing ScenarioAddedEvent: hubId = {}, timestamp = {}",
                    hubEvent.getHubId(), hubEvent.getTimestamp()
            );
            log.debug("ScenarioAddedEvent = {}", hubEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    HubMapper.mapToScenarioAddedEventAvro((ScenarioAddedEvent) hubEvent)));

        }
    },
    SCENARIO_REMOVED {
        @Override
        public void addHubEvent(HubEvent hubEvent,
                                String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing ScenarioRemovedEvent: hubId = {}, timestamp = {}",
                    hubEvent.getHubId(), hubEvent.getTimestamp()
            );
            log.debug("ScenarioRemovedEvent = {}", hubEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    HubMapper.mapToScenarioRemovedEventAvro((ScenarioRemovedEvent) hubEvent)));

        }
    };

    public abstract void addHubEvent(HubEvent hubEvent,
                                     String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer);
}
