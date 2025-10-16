package ru.practicum.event.service;

import ru.practicum.event.model.hub.HubEvent;
import ru.practicum.event.model.sensor.SensorEvent;

public interface EventService {
    void collectSensorEvent(SensorEvent sensorEvent);

    void collectHubEvent(HubEvent hubEvent);
}
