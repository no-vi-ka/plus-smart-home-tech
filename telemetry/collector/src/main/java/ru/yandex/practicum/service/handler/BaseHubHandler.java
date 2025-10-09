package ru.yandex.practicum.service.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafkaConfig.Config;
import ru.yandex.practicum.kafkaConfig.Producer;

@Slf4j
@AllArgsConstructor
public abstract class BaseHubHandler<T extends SpecificRecordBase> implements HubHandler {

    protected final Config kafkaConfig;
    protected final Producer kafkaEventProducer;
    private static final String HUB_TOPIC = "telemetry.hubs.v1";

    protected abstract T mapToAvro(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        T avroEvent = mapToAvro(event);
        if (avroEvent == null) {
            log.error("Failed to map event to Avro: {}", event);
            return;
        }
        try {
            log.info("Sending event {} to topic {}", getMessageType(), HUB_TOPIC);
            kafkaEventProducer.send(HUB_TOPIC, event.getHubId(), avroEvent);
        } catch (Exception e) {
            log.error("Error sending event to Kafka topic {}: {}", HUB_TOPIC, e.getMessage(), e);
        }
    }
}