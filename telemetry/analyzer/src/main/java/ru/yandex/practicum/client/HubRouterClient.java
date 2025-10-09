package ru.yandex.practicum.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.model.Action;


@Slf4j
@Service
public class HubRouterClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterClient(@Value("${grpc.client.hub-router.address}") String address) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
        this.hubRouterClient = HubRouterControllerGrpc.newBlockingStub(channel);
    }


    public void executeAction(Action action, String hubId) {
        DeviceActionRequest request = buildRequest(action, hubId);
        try {
            hubRouterClient.handleDeviceAction(request);
            log.info("Successfully executed action for hub with ID: {}", hubId);
        } catch (StatusRuntimeException e) {
            log.error("Failed to execute action on hub with ID {}. Error: {}", hubId, e.getStatus());
        } catch (Exception e) {
            log.error("Unexpected error while executing action on hub with ID {}: {}", hubId, e.getMessage(), e);
        }
    }

    private DeviceActionRequest buildRequest(Action action, String hubId) {
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(action.getScenario().getName())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeProto.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .build();
    }
}