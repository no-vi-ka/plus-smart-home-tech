package ru.yandex.practicum.aggregator.kafkaconfigs;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${bootstrap_servers}")
    private String boostrapServers;

    @Value("${consumer.client_id}")
    private String clientId;

    @Value("${consumer.group_id}")
    private String groupId;

    @Value("${consumer.key_deserializer_class}")
    private String keyDeserializer;

    @Value("${consumer.value_deserializer_class}")
    private String valueDeserializer;

    @Value("${consumer.enable_auto_commit}")
    private String autoCommit;

    @Bean
    KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);

        return new KafkaConsumer<>(properties);
    }
}
