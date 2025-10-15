package ru.yandex.practicum.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerFactory {

    private final KafkaConfig kafkaConfig;

    @Bean
    public org.springframework.kafka.core.ProducerFactory<Void, SpecificRecordBase> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaConfig.getProducerProperties());
    }

    @Bean
    public KafkaTemplate<Void, SpecificRecordBase> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
