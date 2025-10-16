package ru.yandex.practicum.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@ConfigurationProperties(prefix = "collector.kafka.producer-config")
public class KafkaProducerConfig {
    Properties properties;
    String sensorsTopic;
    String hubsTopic;
}
