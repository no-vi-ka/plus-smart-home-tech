package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.BaseHubEvent;
import ru.yandex.practicum.service.HubEventHandler;
import ru.yandex.practicum.service.kafka.KafkaEventProducer;
import ru.yandex.practicum.service.kafka.ProducerSendParam;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler implements HubEventHandler {

    protected final KafkaEventProducer kafkaEventProducer;
    protected final KafkaProducerConfig kafkaProducerConfig;
    protected final HubEventAvroMapper hubEventAvroMapper;
    protected final HubEventProtoMapper hubEventProtoMapper;

    protected abstract HubEventAvro mapHubToAvro(BaseHubEvent event);

    protected abstract BaseHubEvent mapProtoToHub(HubEventProto eventProto);

    @Override
    public void handle(HubEventProto eventProto) {
        BaseHubEvent event = mapProtoToHub(eventProto);
        log.trace("map to event confirm hubId={}", event.getHubId());
        HubEventAvro avro = mapHubToAvro(event);
        log.trace("map To avro confirm hubId={}", event.getHubId());
        ProducerSendParam param = createProducerSendParam(event, avro);
        log.trace("param created confirm hubId={}", event.getHubId());
        kafkaEventProducer.send(param);
        log.trace("record send confirm hubId={}", event.getHubId());
    }

    protected ProducerSendParam createProducerSendParam(BaseHubEvent event, HubEventAvro avro) {
        return ProducerSendParam.builder()
                .topic(kafkaProducerConfig.getHubsTopic())
                .timestamp(event.getTimestamp().toEpochMilli())
                .key(event.getHubId())
                .value(avro)
                .build();
    }

    protected HubEventAvro buildHubEventAvro(BaseHubEvent event, SpecificRecordBase payloadAvro) {
        return HubEventAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setHubId(event.getHubId())
                .setPayload(payloadAvro)
                .build();
    }
}
