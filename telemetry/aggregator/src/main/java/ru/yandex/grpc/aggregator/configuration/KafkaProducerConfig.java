package ru.yandex.grpc.aggregator.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("aggregator.kafka.producer")
public class KafkaProducerConfig {
    private Properties properties;
    private Map<TopicType, String> topics;
}
