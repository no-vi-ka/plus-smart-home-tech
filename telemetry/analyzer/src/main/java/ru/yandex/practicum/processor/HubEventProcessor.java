package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafkaConfig.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.service.ScenarioService;
import ru.yandex.practicum.service.SensorService;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubConsumer;
    private final SensorService sensorService;
    private final ScenarioService scenarioService;

    public HubEventProcessor(KafkaConfig kafkaConfig, SensorService sensorService, ScenarioService scenarioService) {
        this.hubConsumer = new KafkaConsumer<>(kafkaConfig.getHubConsumerProperties());
        this.sensorService = sensorService;
        this.scenarioService = scenarioService;
    }

    @Override
    public void run() {
        try (hubConsumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
            hubConsumer.subscribe(Collections.singletonList("telemetry.hubs.v1"));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    try {
                        onMessage(record);
                    } catch (Exception e) {
                        log.error("Error processing event: {}", record, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in hubConsumer", e);
        }
    }

    @KafkaListener(topics = "#{@kafkaConfig.topics['hubs-events']}", groupId = "analyzer-hub-group")
    public void onMessage(ConsumerRecord<String, HubEventAvro> record) {
        HubEventAvro event = record.value();
        if (event == null) {
            log.warn("Received null event from Kafka");
            return;
        }

        Object eventPayload = event.getPayload();
        switch (eventPayload) {
            case DeviceAddedEventAvro deviceAddedEvent -> processDeviceAddedEvent(deviceAddedEvent, event.getHubId());
            case DeviceRemovedEventAvro deviceRemovedEvent -> processDeviceRemovedEvent(deviceRemovedEvent, event.getHubId());
            case ScenarioAddedEventAvro scenarioAddedEvent -> processScenarioAddedEvent(scenarioAddedEvent, event.getHubId());
            case ScenarioRemovedEventAvro scenarioRemovedEvent -> processScenarioRemovedEvent(scenarioRemovedEvent);
            case null, default -> {
                assert eventPayload != null;
                log.warn("Unknown event type: {}", eventPayload.getClass().getName());
            }
        }
    }

    private void processDeviceAddedEvent(DeviceAddedEventAvro event, String hubId) {
        sensorService.addSensor(event.getId(), hubId);
        log.info("Sensor added: ID={}, HubID={}", event.getId(), hubId);
    }

    private void processDeviceRemovedEvent(DeviceRemovedEventAvro event, String hubId) {
        sensorService.removeSensor(event.getId(), hubId);
        log.info("Sensor removed: ID={}, HubID={}", event.getId(), hubId);
    }

    private void processScenarioAddedEvent(ScenarioAddedEventAvro event, String hubId) {
        scenarioService.addScenario(event, hubId);
        log.info("Scenario added: Name={}, HubID={}", event.getName(), hubId);
    }

    private void processScenarioRemovedEvent(ScenarioRemovedEventAvro event) {
        scenarioService.deleteScenario(event.getName());
        log.info("Scenario removed: Name={}", event.getName());
    }
}