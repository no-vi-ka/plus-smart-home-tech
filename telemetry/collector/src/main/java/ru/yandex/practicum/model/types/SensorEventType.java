package ru.yandex.practicum.model.types;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.mappers.sensors.SensorMapper;
import ru.yandex.practicum.model.sensor.*;

@Slf4j
public enum SensorEventType {
    CLIMATE_SENSOR_EVENT {
        @Override
        public void addSensorEvent(SensorEvent sensorEvent, String topic,
                                   KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing ClimateSensorEvent: id = {}, hubId = {}, timestamp = {}",
                    sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
            );
            log.debug("ClimateSensorEvent = {}", sensorEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    SensorMapper.mapToClimateSensorAvro(((ClimateSensorEvent) sensorEvent))));
        }
    },
    LIGHT_SENSOR_EVENT {
        @Override
        public void addSensorEvent(SensorEvent sensorEvent, String topic,
                                   KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing LightSensorEvent: id = {}, hubId = {}, timestamp = {}",
                    sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
            );
            log.debug("LightSensorEvent = {}", sensorEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    SensorMapper.mapToLightSensorAvro((LightSensorEvent) sensorEvent)));
        }
    },
    MOTION_SENSOR_EVENT {
        @Override
        public void addSensorEvent(SensorEvent sensorEvent,
                                   String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing MotionSensorEvent: id = {}, hubId = {}, timestamp = {}",
                    sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
            );
            log.debug("MotionSensorEvent = {}", sensorEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    SensorMapper.mapToMotionSensorAvro(((MotionSensorEvent) sensorEvent))));
        }
    },
    SWITCH_SENSOR_EVENT {
        @Override
        public void addSensorEvent(SensorEvent sensorEvent,
                                   String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing SwitchSensorEvent: id = {}, hubId = {}, timestamp = {}",
                    sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
            );
            log.debug("SwitchSensorEvent = {}", sensorEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    SensorMapper.mapToSwitchSensorAvro(((SwitchSensorEvent) sensorEvent))));
        }
    },
    TEMPERATURE_SENSOR_EVENT {
        @Override
        public void addSensorEvent(SensorEvent sensorEvent,
                                   String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
            log.info("Processing TemperatureSensorEvent: id = {}, hubId = {}, timestamp = {}",
                    sensorEvent.getId(), sensorEvent.getHubId(), sensorEvent.getTimestamp()
            );
            log.debug("TemperatureSensorEvent = {}", sensorEvent);
            kafkaProducer.send(new ProducerRecord<>(topic,
                    SensorMapper.mapToTemperatureSensorAvro(((TemperatureSensorEvent) sensorEvent))));
        }
    };

    public abstract void addSensorEvent(SensorEvent sensorEvent,
                                        String topic, KafkaProducer<String, SpecificRecordBase> kafkaProducer);
}
