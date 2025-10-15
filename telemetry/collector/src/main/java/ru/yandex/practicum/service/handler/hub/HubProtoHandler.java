package ru.yandex.practicum.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.CollectorService;

@Slf4j
public abstract class HubProtoHandler implements HubEventHandler {
    @Value("${topic.telemetry.hubs}")
    private String topicTelemetryHubs;

    @Autowired
    private CollectorService service;

    @Override
    public void handle(HubEventProto eventProto) {
        log.info("==> handle hub eventProto = {}", eventProto);
        HubEventAvro eventAvro = mapToAvro(eventProto);

        log.info("<== map for send to eventAvro = {}", eventAvro);
        service.sendEvent(eventAvro, topicTelemetryHubs, eventAvro.getHubId());
    }

    public abstract HubEventAvro mapToAvro(HubEventProto eventProto);
}