package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.EventCollectorService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class CollectorController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final EventCollectorService eventCollectorService;

    public void collectSensorEvent(SensorEventProto eventProto, StreamObserver<Empty> responseObserver) {
        String eventForLog = String.format("ev.payload=%s, ev.hubId=%s, ev.timestamp=%d",
                eventProto.getPayloadCase(),
                eventProto.getHubId(),
                eventProto.getTimestamp().getSeconds());

        try {
            log.info("start collectSensorEvent {}", eventForLog);
            eventCollectorService.collectSensorEvent(eventProto);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.warn("exception collectSensorEvent {}", eventForLog);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }

        log.info("end collectSensorEvent {}", eventForLog);
    }

    public void collectHubEvent(HubEventProto eventProto, StreamObserver<Empty> responseObserver) {
        String eventForLog = String.format("collectHubEvent ev.payload=%s, ev.hubId=%s, ev.timestamp=%d",
                eventProto.getPayloadCase(),
                eventProto.getHubId(),
                eventProto.getTimestamp().getSeconds());

        try {
            log.warn("start collectHubEvent {}", eventForLog);
            eventCollectorService.collectHubEvent(eventProto);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.info("exception collectHubEvent {}", eventForLog);
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }

        log.info("end collectHubEvent {}", eventForLog);
    }
}