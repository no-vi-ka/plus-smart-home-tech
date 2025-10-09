package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.handler.HubHandler;
import ru.yandex.practicum.service.handler.SensorHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final Map<HubEventProto.PayloadCase, HubHandler> hubEventHandlerMap;
    private final Map<SensorEventProto.PayloadCase, SensorHandler> sensorEventHandlerMap;

    public EventController(Set<HubHandler> hubHandlers, Set<SensorHandler> sensorHandlers) {
        this.hubEventHandlerMap = hubHandlers.stream()
                .collect(Collectors.toMap(HubHandler::getMessageType, Function.identity()));
        this.sensorEventHandlerMap = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received Hub event: {}", request);
            HubHandler handler = hubEventHandlerMap.get(request.getPayloadCase());
            if (handler != null) {
                handler.handle(request);
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            } else {
                log.warn("No handler found for Hub event type: {}", request.getPayloadCase());
                responseObserver.onError(new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.withDescription("No handler for this event type")));
            }
        } catch (Exception e) {
            log.error("Error handling Hub event", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)));
        }
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received Sensor event: {}", request);
            SensorHandler handler = sensorEventHandlerMap.get(request.getPayloadCase());
            if (handler != null) {
                handler.handle(request);
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            } else {
                log.warn("No handler found for Sensor event type: {}", request.getPayloadCase());
                responseObserver.onError(new StatusRuntimeException(
                        Status.INVALID_ARGUMENT.withDescription("No handler for this event type")));
            }
        } catch (Exception e) {
            log.error("Error handling Sensor event", e);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)));
        }
    }
}