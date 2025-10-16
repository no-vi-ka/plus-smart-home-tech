package ru.yandex.practicum.shoppingstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum.shoppingstore",
        "ru.yandex.practicum.logging"
})
@EnableFeignClients(basePackages = {
        "ru.yandex.practicum.commerce.client"
})
@EnableAspectJAutoProxy
public class ShoppingStore {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingStore.class, args);
    }
}
