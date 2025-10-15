package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler {
    String getMessageType();

    void handle(HubEventAvro eventAvro);
}
