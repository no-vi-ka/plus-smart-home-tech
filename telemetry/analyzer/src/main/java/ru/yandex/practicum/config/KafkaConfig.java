package ru.yandex.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@Getter
@Setter
public class KafkaConfig {

    @Value("${analyzer.kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${analyzer.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${analyzer.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${analyzer.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;

    @Value("${analyzer.kafka.hub.group-id}")
    private String hubGroupId;

    @Value("${analyzer.kafka.consumer.hubs.value-deserializer}")
    private String hubValueDeserializer;

    @Value("${analyzer.kafka.snapshot.group-id}")
    private String snapshotGroupId;

    @Value("${analyzer.kafka.consumer.snapshots.value-deserializer}")
    private String snapshotValueDeserializer;

    @Value("${spring.kafka.consumer.poll-timeout-ms:1000}")
    private int pollTimeoutMs;


    @Bean
    public Properties hubConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubValueDeserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // hub авто-коммитим
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200);
        return props;
    }


    @Bean
    public Properties snapshotConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotValueDeserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // снапшоты коммитим вручную
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        return props;
    }
}
