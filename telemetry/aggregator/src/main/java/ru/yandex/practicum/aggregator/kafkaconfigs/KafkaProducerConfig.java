package ru.yandex.practicum.aggregator.kafkaconfigs;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Value("${bootstrap_servers}")
    private String boostrapServers;

    @Value("${producer.key_serializer_class}")
    private String keySerializer;

    @Value("${producer.value_serializer_class}")
    private String valueSerializer;

    @Bean
    KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        return new KafkaProducer<>(properties);
    }
}
