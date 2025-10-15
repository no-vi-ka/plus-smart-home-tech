package ru.yandex.practicum.service.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler(KafkaConfig kafkaConfig, KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate) {
        super(kafkaConfig, kafkaTemplate);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    protected ScenarioRemovedEventAvro mapToAvro(HubEventProto eventProto) {
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(eventProto.getScenarioRemoved().getName())
                .build();
    }

}
