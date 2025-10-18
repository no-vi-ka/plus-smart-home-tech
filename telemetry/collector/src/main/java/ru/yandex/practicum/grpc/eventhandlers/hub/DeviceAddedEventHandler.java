package ru.yandex.practicum.grpc.eventhandlers.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    @Value("${topics.hubs}")
    private String hubTopic;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().getSeconds(),
                        hubEvent.getTimestamp().getNanos()))
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(hubEvent.getDeviceAdded().getId())
                        .setType(mapToDeviceTypeAvro(hubEvent.getDeviceAdded().getDeviceType()))
                        .build()
                )
                .build();

        kafkaProducer.send(new ProducerRecord<>(hubTopic, hubEventAvro));

    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceTypeProto type) {
        switch (type) {
            case LIGHT_SENSOR -> {
                return DeviceTypeAvro.LIGHT_SENSOR;
            }
            case MOTION_SENSOR -> {
                return DeviceTypeAvro.MOTION_SENSOR;
            }
            case SWITCH_SENSOR -> {
                return DeviceTypeAvro.SWITCH_SENSOR;
            }
            case TEMPERATURE_SENSOR -> {
                return DeviceTypeAvro.TEMPERATURE_SENSOR;
            }
            case CLIMATE_SENSOR -> {
                return DeviceTypeAvro.CLIMATE_SENSOR;
            }
            default -> {
                return null;
            }
        }
    }
}
