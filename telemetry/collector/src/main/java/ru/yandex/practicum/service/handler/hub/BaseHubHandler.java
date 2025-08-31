package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.config.KafkaTopicsNames;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;
import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;
    protected final KafkaTopicsNames topicsNames;

    @Override
    public void handle(HubEvent event) {

        if (event == null) {
            throw new IllegalArgumentException("HubEvent cannot be null");
        }
        log.debug("instance check confirm hubId={}", event.getHubId());
        HubEventAvro avro = mapToAvroHubEvent(event);
        log.debug("map To avro confirm hubId={}", event.getHubId());
        ProducerRecord<String, SpecificRecordBase> param = createProducerSendParam(event, avro);
        log.debug("param created confirm hubId={}", event.getHubId());
        producer.sendRecord(param.topic(), param.timestamp(), param.key(), param.value());
        log.debug("record send confirm hubId={}", event.getHubId());
    }

    @Override
    public HubEventType getMessageType() {
        throw new UnsupportedOperationException("Метод должен быть переопределен в наследнике");
    }

    protected HubEventAvro buildHubEventAvro(HubEvent hubEvent, T payloadAvro) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }

    private ProducerRecord<String, SpecificRecordBase> createProducerSendParam(HubEvent event, HubEventAvro avro) {
        return new ProducerRecord<>(topicsNames.getHubsTopic(), null, event.getTimestamp().toEpochMilli(), event.getHubId(), avro);
    }

    protected abstract SpecificRecordBase mapToAvro(HubEvent hubEvent);

    protected abstract HubEventAvro mapToAvroHubEvent(HubEvent hubEvent);
}
