package ru.yandex.practicum.util;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.HubEventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class HubEventHandleFactory {
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEvent;

    public HubEventHandleFactory(Set<HubEventHandler> handlers) {
        hubEvent = new HashMap<>();
        handlers.forEach(handler -> hubEvent.put(handler.getHubEventType().getProtoPayloadCase(), handler));
    }

    public HubEventHandler getHubEventHandlerByPayloadCase(HubEventProto.PayloadCase payloadCase) {
        return Optional.ofNullable(hubEvent.get(payloadCase))
                .orElseThrow(() -> new NotFoundException(payloadCase + " not exist in map"));
    }
}
