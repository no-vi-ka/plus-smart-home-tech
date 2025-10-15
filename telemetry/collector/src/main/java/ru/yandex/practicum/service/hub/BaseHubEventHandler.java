package ru.yandex.practicum.service.hub;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventHandler;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    protected final KafkaConfig kafkaConfig;
    protected final KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate;
    private final String hubs = "hubs";

    protected abstract T mapToAvro(HubEventProto eventProto);

    @Override
    public void handle(HubEventProto eventProto) {
        if (!eventProto.getPayloadCase().equals(getMessageType())) {
            throw new IllegalArgumentException(String.format("Неизвестный тип события: %s", eventProto.getPayloadCase()));
        }

        T payload = mapToAvro(eventProto);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(eventProto.getHubId())
                .setTimestamp(mapToInstant(eventProto.getTimestamp()))
                .setPayload(payload)
                .build();

        kafkaTemplate.send(kafkaConfig.getTopics().get(hubs), eventAvro);
    }

    protected Instant mapToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(
                timestamp.getSeconds(),
                timestamp.getNanos()
        );
    }

}
