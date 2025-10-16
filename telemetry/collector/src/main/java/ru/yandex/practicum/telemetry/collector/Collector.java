package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Collector {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Collector.class, args);
    }
}
