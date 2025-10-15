package ru.yandex.practicum.config.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("kafka")
@Getter
@RequiredArgsConstructor
public class KafkaConfig {
    private final Map<String, String> topics;
    private final Map<String, Object> producerProperties;
}
