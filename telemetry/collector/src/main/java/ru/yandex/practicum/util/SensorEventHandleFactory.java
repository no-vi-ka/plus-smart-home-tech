package ru.yandex.practicum.util;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.SensorEventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class SensorEventHandleFactory {
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorHandlers;

    public SensorEventHandleFactory(Set<SensorEventHandler> handlers) {
        sensorHandlers = new HashMap<>();
        handlers.forEach(handler -> sensorHandlers.put(handler.getSensorEventType().getProtoPayloadCase(), handler));
    }

    public SensorEventHandler getSensorEventHandlerByPayloadCase(SensorEventProto.PayloadCase payloadCase) {
        return Optional.ofNullable(sensorHandlers.get(payloadCase))
                .orElseThrow(() -> new NotFoundException(payloadCase + " not exist in map"));
    }
}
