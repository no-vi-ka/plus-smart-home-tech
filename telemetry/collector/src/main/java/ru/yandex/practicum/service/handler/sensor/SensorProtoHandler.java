package ru.yandex.practicum.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.CollectorService;

@Slf4j
public abstract class SensorProtoHandler implements SensorEventHandler {
    @Value("${topic.telemetry.sensors}")
    private String topicTelemetrySensors;

    @Autowired
    private CollectorService service;

    @Override
    public void handle(SensorEventProto eventProto) {
        log.info("==> handle sensor eventProto = {}", eventProto);
        SensorEventAvro eventAvro = mapToAvro(eventProto);

        service.sendEvent(eventAvro, topicTelemetrySensors, eventAvro.getHubId());
    }

    public abstract SensorEventAvro mapToAvro(SensorEventProto eventProto);
}