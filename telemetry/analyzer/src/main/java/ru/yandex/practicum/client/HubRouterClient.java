package ru.yandex.practicum.client;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.service.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.mapper.EnumMapper;
import ru.yandex.practicum.model.Action;

import java.time.Instant;

@Service
@Slf4j
public class HubRouterClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterClient(@GrpcClient("hub-router")
                           HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendAction(Action action) {
        DeviceActionRequestProto DeviceActionRequestProto = buildActionRequest(action);
        hubRouterClient.handleDeviceAction(DeviceActionRequestProto);
        log.info("Действие {} отправлено в hub-router", DeviceActionRequestProto);
    }

    private DeviceActionRequestProto buildActionRequest(Action action) {
        return DeviceActionRequestProto.newBuilder()
                .setHubId(action.getScenario().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensor().getId())
                        .setType(EnumMapper.mapEnum(action.getType(), ActionTypeProto.class))
                        .setValue(action.getValue())
                        .build())
                .setTimestamp(setTimestamp())
                .build();
    }
    private Timestamp setTimestamp() {
        Instant instant = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

}
