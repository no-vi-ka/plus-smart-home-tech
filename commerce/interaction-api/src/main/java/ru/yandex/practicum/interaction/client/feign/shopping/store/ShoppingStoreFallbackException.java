package ru.yandex.practicum.interaction.client.feign.shopping.store;

public class ShoppingStoreFallbackException extends RuntimeException {
    public ShoppingStoreFallbackException() {
        super("shopping-store временно недоступен");
    }
}
