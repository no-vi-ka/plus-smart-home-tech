package ru.practicum.event_deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventAvroDeserializer extends BaseAvroDeserializer<SensorEventAvro> {

    public SensorEventAvroDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
