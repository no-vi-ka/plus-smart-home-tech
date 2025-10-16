package ru.yandex.practicum.service.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.BaseHubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.hub.device.DeviceRemovedEvent;
import ru.yandex.practicum.service.kafka.KafkaEventProducer;

@Component
public class DeviceRemovedEventHandler extends BaseHubEventHandler {
    public DeviceRemovedEventHandler(KafkaEventProducer kafkaEventProducer,
                                     KafkaProducerConfig kafkaProducerConfig,
                                     HubEventAvroMapper hubEventAvroMapper,
                                     HubEventProtoMapper hubEventProtoMapper) {
        super(kafkaEventProducer, kafkaProducerConfig, hubEventAvroMapper, hubEventProtoMapper);
    }

    @Override
    protected HubEventAvro mapHubToAvro(BaseHubEvent event) {
        DeviceRemovedEventAvro avro = hubEventAvroMapper.mapToDeviceRemovedEventAvro((DeviceRemovedEvent) event);
        return buildHubEventAvro(event, avro);
    }

    @Override
    protected BaseHubEvent mapProtoToHub(HubEventProto eventProto) {
        BaseHubEvent event = hubEventProtoMapper.mapToDeviceRemovedEvent(eventProto.getDeviceRemoved());
        return hubEventProtoMapper.mapBaseFields(event, eventProto);
    }

    @Override
    public HubEventType getHubEventType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
