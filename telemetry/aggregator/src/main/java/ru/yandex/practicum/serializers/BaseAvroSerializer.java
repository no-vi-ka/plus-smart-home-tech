package ru.yandex.practicum.serializers;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class BaseAvroSerializer<T extends GenericRecord> implements Serializer<T> {

    @Override
    public byte[] serialize(String topic, T data) {
        final EncoderFactory encoderFactory = EncoderFactory.get();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (data != null) {
                if (data.getSchema() == null) {
                    throw new SerializationException("Schema cannot be null for the record");
                }

                BinaryEncoder encoder = encoderFactory.binaryEncoder(out, null);
                DatumWriter<T> writer = new SpecificDatumWriter<>(data.getSchema());
                writer.write(data, encoder);
                encoder.flush();
                return out.toByteArray();
            }
            return null;
        } catch (IOException ex) {
            throw new SerializationException("Data deserialization error for the topic: " + topic, ex);
        }
    }
}