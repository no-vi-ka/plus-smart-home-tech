package ru.yandex.practicum.model.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import static ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.*;

public enum SensorEventType {
    MOTION_SENSOR_EVENT(PayloadCase.MOTION_SENSOR_EVENT),
    TEMPERATURE_SENSOR_EVENT(SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT),
    LIGHT_SENSOR_EVENT(SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT),
    CLIMATE_SENSOR_EVENT(PayloadCase.CLIMATE_SENSOR_EVENT),
    SWITCH_SENSOR_EVENT(PayloadCase.SWITCH_SENSOR_EVENT);

    private final SensorEventProto.PayloadCase payloadCase;

    SensorEventType(SensorEventProto.PayloadCase payloadCase) {
        this.payloadCase = payloadCase;
    }

    public SensorEventProto.PayloadCase getProtoPayloadCase() {
        return payloadCase;
    }
}
