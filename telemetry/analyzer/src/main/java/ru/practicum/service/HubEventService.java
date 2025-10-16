package ru.practicum.service;

import ru.practicum.model.Scenario;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventService {
    void process(HubEventAvro hubEventAvro);

    void sendActionsByScenario(Scenario scenario);
}
