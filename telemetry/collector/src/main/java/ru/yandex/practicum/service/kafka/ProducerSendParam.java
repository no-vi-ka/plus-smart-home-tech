package ru.yandex.practicum.service.kafka;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.avro.specific.SpecificRecordBase;

@Builder
@Getter
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProducerSendParam {
    String topic;
    Integer partition;
    Long timestamp;
    String key;
    SpecificRecordBase value;

    public boolean isValid() {
        return topic != null && timestamp != null && key != null && value != null;
    }
}
