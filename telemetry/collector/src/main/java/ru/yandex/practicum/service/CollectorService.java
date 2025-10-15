package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.constant.HubEventType;
import ru.yandex.practicum.constant.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {
    @Value("${topic.telemetry.sensors}")
    private String topicTelemetrySensors;
    @Value("${topic.telemetry.hubs}")
    private String topicTelemetryHubs;
    @Value("${kafka.bootstrap-servers}")
    private String kafkaServerAddress;

    public void sendSensorEvent(SensorEvent sensorEvent) {
        switch (sensorEvent.getType()) {
            case SensorEventType.CLIMATE_SENSOR_EVENT:
                ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) sensorEvent;
                ClimateSensorAvro climateSensorAvro = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(climateSensorEvent.getTemperatureC())
                        .setCo2Level(climateSensorEvent.getCo2Level())
                        .setHumidity(climateSensorEvent.getHumidity())
                        .build();

                sendEvent(climateSensorAvro, topicTelemetrySensors, sensorEvent.getHubId());
                break;
            case SensorEventType.LIGHT_SENSOR_EVENT:
                LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;
                LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                        .setLinkQuality(lightSensorEvent.getLinkQuality())
                        .setLuminosity(lightSensorEvent.getLuminosity())
                        .build();
                sendEvent(lightSensorAvro, topicTelemetrySensors, sensorEvent.getHubId());
                break;
            case SensorEventType.MOTION_SENSOR_EVENT:
                MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;
                MotionSensorAvro motionSensorAvro = MotionSensorAvro.newBuilder()
                        .setMotion(motionSensorEvent.isMotion())
                        .setLinkQuality(motionSensorEvent.getLinkQuality())
                        .setVoltage(motionSensorEvent.getVoltage())
                        .build();
                sendEvent(motionSensorAvro, topicTelemetrySensors, sensorEvent.getHubId());
                break;
            case SensorEventType.SWITCH_SENSOR_EVENT:
                SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) sensorEvent;
                SwitchSensorAvro switchSensorAvro = SwitchSensorAvro.newBuilder()
                        .setState(switchSensorEvent.isState())
                        .build();
                sendEvent(switchSensorAvro, topicTelemetrySensors, sensorEvent.getHubId());
                break;
            case SensorEventType.TEMPERATURE_SENSOR_EVENT:
                TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) sensorEvent;
                TemperatureSensorAvro temperatureSensorAvro = TemperatureSensorAvro.newBuilder()
                        .setId(temperatureSensorEvent.getId())
                        .setHubId(temperatureSensorEvent.getHubId())
                        .setTimestamp(temperatureSensorEvent.getTimestamp())
                        .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                        .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                        .build();
                sendEvent(temperatureSensorAvro, topicTelemetrySensors, sensorEvent.getHubId());
        }
    }

    public void sendHubEvent(HubEvent hubEvent) {
        switch (hubEvent.getType()) {
            case HubEventType.DEVICE_ADDED:
                DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) hubEvent;
                DeviceAddedEventAvro deviceAddedEventAvro = DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(deviceAddedEvent.getDeviceType())
                        .build();
                sendEvent(deviceAddedEventAvro, topicTelemetryHubs, hubEvent.getHubId());
                break;
            case HubEventType.DEVICE_REMOVED:
                DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) hubEvent;
                DeviceRemovedEventAvro deviceRemovedEventAvro = DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build();
                sendEvent(deviceRemovedEventAvro, topicTelemetryHubs, hubEvent.getHubId());
                break;
            case HubEventType.SCENARIO_ADDED:
                ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;
                ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setActions(scenarioAddedEvent.getActions())
                        .setConditions(scenarioAddedEvent.getConditions())
                        .build();
                sendEvent(scenarioAddedEventAvro, topicTelemetryHubs, hubEvent.getHubId());
                break;
            case HubEventType.SCENARIO_REMOVED:
                ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) hubEvent;
                ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build();
                sendEvent(scenarioRemovedEvent, topicTelemetryHubs, hubEvent.getHubId());
                break;
        }
    }

    public <T> void sendEvent(T event, String topic, String hubId) {
        log.info("==> Send event = {}, topic = {}, hubId = {}", event, topic, hubId);

        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerAddress);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);

        ProducerRecord<String, T> record = new ProducerRecord<>(topic, hubId, event);

        try (Producer<String, T> producer = new KafkaProducer<>(config)) {
            log.info("<== Send the event in partition = {}", record.partition());
            producer.send(record);
            producer.flush();
            producer.close();
        }
    }
}
