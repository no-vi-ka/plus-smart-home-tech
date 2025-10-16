package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("collector.kafka")
public class KafkaConfig {
    private ProducerConfig producer;
    private Map<TopicType, String> topics;

    @Getter
    @Setter
    public static class ProducerConfig {
        private Properties properties;
    }
}
