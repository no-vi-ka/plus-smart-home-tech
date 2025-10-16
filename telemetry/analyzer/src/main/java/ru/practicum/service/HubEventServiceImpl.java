package ru.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.handler.hub.HubEventHandler;
import ru.practicum.model.Action;
import ru.practicum.model.Scenario;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HubEventServiceImpl implements HubEventService {
    @GrpcClient("hub-router")
    HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;
    private final Map<String, HubEventHandler> hubEventHandlers;

    public HubEventServiceImpl(Set<HubEventHandler> hubEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        Function.identity()
                ));
    }

    @Override
    public void process(HubEventAvro hubEventAvro) {
        log.info("start process for {}", hubEventAvro);
        String type = hubEventAvro.getPayload().getClass().getName();
        if (hubEventHandlers.containsKey(type)) {
            log.info("process {}", hubEventHandlers.get(type));
            hubEventHandlers.get(type).handle(hubEventAvro);
        } else {
            throw new IllegalArgumentException("Не могу найти обработчик для события " + type);
        }
    }

    @Override
    public void sendActionsByScenario(Scenario scenario) {
        log.info("send scenario: {} with {} actions", scenario, scenario.getActions().size());
        String hubId = scenario.getHubId();
        String scenarioName = scenario.getName();
        for (Action action : scenario.getActions()) {
            Instant ts = Instant.now();
            DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                    .setSensorId(action.getSensor().getId())
                    .setType(ActionTypeProto.valueOf(action.getType().name()))
                    .setValue(action.getValue())
                    .build();
            DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenarioName)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(ts.getEpochSecond())
                            .setNanos(ts.getNano()))
                    .setAction(deviceActionProto)
                    .build();
            hubRouterClient.handleDeviceAction(deviceActionRequest);
            log.info("action was sent: {}", deviceActionRequest);
        }

    }
}
