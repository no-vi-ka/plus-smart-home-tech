package ru.yandex.practicum.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.security.SecureRandom;
import java.util.Random;

@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum.warehouse",
        "ru.yandex.practicum.logging"
})
@EnableFeignClients(basePackages = {
        "ru.yandex.practicum.commerce.client"
})
@EnableAspectJAutoProxy
public class WarehouseApp {
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    public static void main(String[] args) {
        SpringApplication.run(WarehouseApp.class, args);
    }

    public static String getCurrentAddress() {
        return CURRENT_ADDRESS;
    }

}
