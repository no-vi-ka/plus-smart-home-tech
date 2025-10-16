package ru.yandex.practicum.shopping.store.config;

import feign.Feign;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.interaction.client.feign.decoder.FeignErrorDecoder;

@Configuration
@EnableFeignClients(basePackages = {"ru.yandex.practicum.interaction"})
public class FeignConfig {
    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .errorDecoder(new FeignErrorDecoder());
    }
}
