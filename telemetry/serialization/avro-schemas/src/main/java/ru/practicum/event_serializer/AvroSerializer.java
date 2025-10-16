package ru.practicum.event_serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;

public class AvroSerializer implements Serializer<SpecificRecordBase> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();
    private BinaryEncoder encoder;
    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (data != null) {
                DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());
                encoder = encoderFactory.binaryEncoder(out, encoder);
                writer.write(data, encoder);
                encoder.flush();
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Avro object", e);
        }
    }
}
