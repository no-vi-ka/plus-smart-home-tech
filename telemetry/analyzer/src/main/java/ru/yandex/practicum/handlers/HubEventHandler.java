package ru.yandex.practicum.handlers;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler {
    String getMessageType();

    void handle(HubEventAvro hubEvent);
}
