package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class ScenarioRemovedEventHandler extends BaseHubEventHandler<ScenarioRemovedEventAvro> {
    public ScenarioRemovedEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public ScenarioRemovedEventAvro mapToAvro(HubEventProto eventProto) {
        ScenarioRemovedEventProto payload = eventProto.getScenarioRemoved();
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(payload.getName())
                .build();
    }
}
