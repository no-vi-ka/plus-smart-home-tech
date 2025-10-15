package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.CollectorService;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class CollectorController {
    private final CollectorService service;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("==> collect sensorEvent = {}", sensorEvent);
        service.sendSensorEvent(sensorEvent);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("==> collect hubEvent = {}", hubEvent);
        service.sendHubEvent(hubEvent);
    }
}
