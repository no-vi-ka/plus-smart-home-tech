package ru.yandex.practicum.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Configuration
public class GrpcClientConfig {

    @Bean
    public HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 59090)
                .usePlaintext()
                .build();
        return HubRouterControllerGrpc.newBlockingStub(channel);
    }
}
