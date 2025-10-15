package ru.yandex.practicum.config;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.exception.CustomErrorDecoder;

@Configuration
public class FeignConfig {
    @Bean
    public Feign.Builder getFeignBuilder() {
        return Feign.builder().errorDecoder(new CustomErrorDecoder());
    }
}
