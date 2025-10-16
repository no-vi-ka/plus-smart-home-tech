package ru.yandex.practicum.interaction.client.feign.shopping.cart;

public class ShoppingCartFallbackException extends RuntimeException {
    public ShoppingCartFallbackException() {
        super("shopping-cart временно недоступен");
    }
}
