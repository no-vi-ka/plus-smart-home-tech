package ru.yandex.practicum.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@ConfigurationProperties
@RequiredArgsConstructor
public class CollectorConfiguration {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String boostrapServer;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializerClass;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializerClass;


    @Bean
    KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);

        return new KafkaProducer<>(config);
    }
}
