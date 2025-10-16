package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorHandlers,
                           Set<HubEventHandler> hubHandlers) {
        this.sensorEventHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getType, Function.identity()));
        this.hubEventHandlers = hubHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto event, StreamObserver<Empty> responseObserver) {
        try {
            log.info("==> Add sensor event: {}", event);
            if (sensorEventHandlers.containsKey(event.getPayloadCase())) {
                sensorEventHandlers.get(event.getPayloadCase()).handle(event);
            } else {
                log.warn("No sensor handler for event type: {}", event.getPayloadCase());
                throw new IllegalArgumentException("No handler for event type: " + event.getPayloadCase());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.fromThrowable(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto event, StreamObserver<Empty> responseObserver) {
        try {
            log.info("==> Add hub event: {}", event);
            if (hubEventHandlers.containsKey(event.getPayloadCase())) {
                hubEventHandlers.get(event.getPayloadCase()).handle(event);
            } else {
                log.warn("No hub handler for event type: {}", event.getPayloadCase());
                throw new IllegalArgumentException("No handler for event type: " + event.getPayloadCase());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.fromThrowable(e)
            ));
        }
    }
}
