package ru.yandex.practicum.service.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.mapper.EnumMapper;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaConfig kafkaConfig, KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate) {
        super(kafkaConfig, kafkaTemplate);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEventProto eventProto) {
        DeviceAddedEventProto deviceAddedEvent = eventProto.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(EnumMapper.mapEnum(deviceAddedEvent.getType(), DeviceTypeAvro.class))
                .build();
    }

}
