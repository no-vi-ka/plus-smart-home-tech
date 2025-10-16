package ru.yandex.practicum.interaction.exception.shopping.cart;

import ru.yandex.practicum.interaction.exception.BaseServiceException;

public class ShoppingCartDeactivateException extends BaseServiceException {
    public ShoppingCartDeactivateException() {
        this.httpStatus = "400";
        this.userMessage = "корзина деактивирована";
    }
}
