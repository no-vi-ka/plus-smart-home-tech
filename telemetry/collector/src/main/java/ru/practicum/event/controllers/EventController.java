package ru.practicum.event.controllers;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.event.mapper.hub.HubEventHandler;
import ru.practicum.event.mapper.sensor.SensorEventHandler;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.SensorEvent;
import ru.practicum.event.sevices.EventService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubHandlers;
    private final EventService eventService;

    public EventController(
            Set<SensorEventHandler> sensorHandlers,
            Set<HubEventHandler> hubHandlers,
            EventService eventService
    ) {
        this.sensorHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, h -> h));

        this.hubHandlers = hubHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, h -> h));

        this.eventService = eventService;
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("Выполняю запрос по сенсорам с телом {}", request);
        try {
            SensorEventHandler handler = sensorHandlers.get(request.getPayloadCase());
            if (handler == null) {
                throw new IllegalArgumentException("No handler for " + request.getPayloadCase());
            }

            SensorEvent javaEvent = handler.toJava(request);
            eventService.processSensor(javaEvent);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        log.info("Выполняю запрос по хабам с телом {}", request);
        try {
            HubEventHandler handler = hubHandlers.get(request.getPayloadCase());
            if (handler == null) {
                throw new IllegalArgumentException("No handler for " + request.getPayloadCase());
            }

            HubEvent javaEvent = handler.toJava(request);
            eventService.processHub(javaEvent);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.withDescription(e.getMessage()).withCause(e)
            ));
        }
    }
}

