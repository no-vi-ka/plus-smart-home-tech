package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.config.KafkaTopicsNames;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.enums.HubEventType;

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
        log.trace("instance check confirm hubId={}", event.getHubId());
        HubEventAvro avro = mapToAvroHubEvent(event);
        log.trace("map To avro confirm hubId={}", event.getHubId());
        ProducerRecord<String, SpecificRecordBase> param = createProducerSendParam(event, avro);
        log.debug("param created confirm hubId={}", event.getHubId());
        producer.sendRecord(param);
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


//package ru.yandex.practicum.service.handler.hub;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.avro.specific.SpecificRecordBase;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
//import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
//import ru.yandex.practicum.kafka.KafkaEventProducer;
//import ru.yandex.practicum.kafka.config.KafkaTopicsNames;
//import ru.yandex.practicum.model.hub.HubEvent;
//import ru.yandex.practicum.service.mapper.hub.HubEventAvroMapper;
//import ru.yandex.practicum.service.mapper.hub.HubEventProtoMapper;
//
//import java.time.Instant;
//
//@Slf4j
//@RequiredArgsConstructor
//public abstract class BaseHubHandler implements HubEventHandler {
//
//    protected final KafkaEventProducer producer;
//    protected final KafkaTopicsNames topicsNames;
//    protected final HubEventAvroMapper avroMapper;
//    protected final HubEventProtoMapper protoMapper;
//
//    protected abstract HubEventAvro mapHubToAvro(HubEvent hubEvent);
//
//    protected abstract HubEvent mapHubProtoToModel(HubEventProto hubProto);
//
//    @Override
//    public void handle(HubEventProto hubProto) {
//
//        if (hubProto == null) {
//            throw new IllegalArgumentException("HubEvent cannot be null");
//        }
//        HubEvent event = mapHubProtoToModel(hubProto);
//        log.debug("map To HUB confirm hubId={}", event.getHubId());
//        HubEventAvro avro = mapHubToAvro(event);
//        log.debug("map To AVRO confirm hubId={}", event.getHubId());
//        ProducerRecord<String, SpecificRecordBase> param = createProducerSendParam(event, avro);
//        log.debug("param created confirm hubId={}", event.getHubId());
//        producer.sendRecord(param);
//        log.debug("record send confirm hubId={}", event.getHubId());
//    }
//
//    protected HubEventAvro buildHubEventAvro(HubEvent hubEvent, SpecificRecordBase payloadAvro) {
//        return HubEventAvro.newBuilder()
//                .setHubId(hubEvent.getHubId())
//                .setTimestamp(hubEvent.getTimestamp())
//                .setPayload(payloadAvro)
//                .build();
//    }
//
//    protected HubEvent mapBaseHubProtoFieldsToHub(HubEvent hub, HubEventProto hubProto) {
//        hub.setHubId(hubProto.getHubId());
//
//        long seconds = hubProto.getTimestamp().getSeconds();
//        int nanos = hubProto.getTimestamp().getNanos();
//
//        hub.setTimestamp(Instant.ofEpochSecond(seconds, nanos));
//        return hub;
//    }
//
//    private ProducerRecord<String, SpecificRecordBase> createProducerSendParam(HubEvent event, HubEventAvro avro) {
//        return new ProducerRecord<>(topicsNames.getHubsTopic(), null, event.getTimestamp().toEpochMilli(), event.getHubId(), avro);
//    }
//}
