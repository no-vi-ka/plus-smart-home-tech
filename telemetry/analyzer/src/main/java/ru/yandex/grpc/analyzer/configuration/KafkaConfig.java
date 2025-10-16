package ru.yandex.grpc.analyzer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("analyzer.kafka")
public class KafkaConfig {
    private ConsumerProperties hubConsumer;
    private ConsumerProperties snapshotConsumer;
    private Map<TopicType, String> topics;

    @Getter
    @Setter
    public static class ConsumerProperties {
        private Properties properties;
    }
}
