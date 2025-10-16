package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.util.HubEventHandleFactory;
import ru.yandex.practicum.util.SensorEventHandleFactory;

@RequiredArgsConstructor
@Service
public class EventCollectorServiceImpl implements EventCollectorService {
    private static final Logger log = LoggerFactory.getLogger(EventCollectorServiceImpl.class);
    private final SensorEventHandleFactory sensorEventHandleFactory;
    private final HubEventHandleFactory hubEventHandleFactory;

    @Override
    public void collectHubEvent(HubEventProto event) {
        log.info("collect hub: ev.payload={}, ev.hubId={}, ev.timestamp={}",
                event.getPayloadCase(),
                event.getHubId(),
                event.getTimestamp());
        hubEventHandleFactory.getHubEventHandlerByPayloadCase(event.getPayloadCase()).handle(event);
    }

    @Override
    public void collectSensorEvent(SensorEventProto event) {
        log.info("collect sensor: ev.payload={}, ev.hubId={}, ev.timestamp={}",
                event.getPayloadCase(),
                event.getHubId(),
                event.getTimestamp());
        sensorEventHandleFactory.getSensorEventHandlerByPayloadCase(event.getPayloadCase()).handle(event);
    }
}
