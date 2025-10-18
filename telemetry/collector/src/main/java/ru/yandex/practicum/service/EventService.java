package ru.yandex.practicum.service;

import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

public interface EventService {
    SensorEvent addSensorEvent(SensorEvent sensorEvent);

    HubEvent addHubEvent(HubEvent hubEvent);
}
