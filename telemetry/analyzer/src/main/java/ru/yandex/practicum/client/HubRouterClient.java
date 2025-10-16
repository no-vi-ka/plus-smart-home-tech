package ru.yandex.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

import com.google.protobuf.Empty;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRouterClient {


    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    public void sendAction(DeviceActionRequest request) {
        try {
            hubRouterStub.handleDeviceAction(request);
            log.info("Команда отправлена на Hub Router: {}", request);
        } catch (Exception e) {
            log.error("Не удалось отправить команду на Hub Router", e);
        }
    }
}

