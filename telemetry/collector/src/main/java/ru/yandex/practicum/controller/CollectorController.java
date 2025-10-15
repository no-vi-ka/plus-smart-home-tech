package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.service.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.HubEventHandler;
import ru.yandex.practicum.service.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;

    public CollectorController(Set<HubEventHandler> hubEventHandlers, Set<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto sensorEvent, StreamObserver<Empty> responseObserver) {
        try {
            sensorEventHandlers.get(sensorEvent.getPayloadCase()).handle(sensorEvent);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
        responseObserver.onError(new StatusRuntimeException(
                Status.INTERNAL
                        .withDescription(e.getLocalizedMessage())
                        .withCause(e)
        ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto hubEvent, StreamObserver<Empty> responseObserver) {
        try {
            hubEventHandlers.get(hubEvent.getPayloadCase()).handle(hubEvent);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
