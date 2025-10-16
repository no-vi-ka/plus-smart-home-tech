package ru.practicum.event_deserializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventAvroDeserializer extends BaseAvroDeserializer<HubEventAvro> {

    public HubEventAvroDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}


