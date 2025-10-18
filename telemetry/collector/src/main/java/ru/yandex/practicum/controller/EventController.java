package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.EventService;

@Slf4j
@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/sensors")
    public SensorEvent collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Got sensor event with id = {}", sensorEvent.getId());
        log.debug("Event = {}", sensorEvent);
        SensorEvent response =  eventService.addSensorEvent(sensorEvent);
        log.info("Send response with id = {}", response.getId());
        return response;
    }

    @PostMapping("/hubs")
    public HubEvent collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Got hub event with hub id = {}", hubEvent.getHubId());
        log.debug("Event = {}", hubEvent);
        HubEvent response = eventService.addHubEvent(hubEvent);
        log.info("Send response with hub id = {}", hubEvent.getHubId());
        return response;
    }
}
