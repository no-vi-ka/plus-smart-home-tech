package ru.yandex.practicum.commerce.client.warehouse;

import feign.Feign;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .errorDecoder(new CustomErrorDecoder())
                .logLevel(Logger.Level.FULL);
    }
}
