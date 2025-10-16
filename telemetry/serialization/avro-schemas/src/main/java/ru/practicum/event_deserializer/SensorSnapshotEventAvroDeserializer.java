package ru.practicum.event_deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorSnapshotEventAvroDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {

    public SensorSnapshotEventAvroDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
