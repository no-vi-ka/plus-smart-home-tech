package ru.yandex.practicum.serializers;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.lang.NonNull;

import java.io.ByteArrayInputStream;


public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {
    private final Schema schema;
    private final DecoderFactory decoderFactory;


    public BaseAvroDeserializer(@NonNull Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(@NonNull DecoderFactory decoderFactory, @NonNull Schema schema) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            DatumReader<T> reader = new SpecificDatumReader<>(schema);
            return reader.read(null, decoderFactory.binaryDecoder(inputStream, null));
        } catch (Exception e) {
            throw new SerializationException("Data deserialization error for the topic: " + topic, e);
        }
    }
}