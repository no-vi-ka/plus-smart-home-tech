package ru.yandex.practicum.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
    scanBasePackages = {
            "ru.yandex.practicum.shoppingcart",
            "ru.yandex.practicum.logging"
    })
@EnableAspectJAutoProxy
@EnableFeignClients(basePackages = {
        "ru.yandex.practicum.commerce.client"
})
public class ShoppingCartApp {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApp.class, args);
    }

}
