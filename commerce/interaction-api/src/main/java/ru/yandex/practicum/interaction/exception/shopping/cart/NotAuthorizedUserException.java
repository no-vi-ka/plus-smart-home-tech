package ru.yandex.practicum.interaction.exception.shopping.cart;

import ru.yandex.practicum.interaction.exception.BaseServiceException;

public class NotAuthorizedUserException extends BaseServiceException {
    public NotAuthorizedUserException() {
        this.httpStatus = "401";
        this.userMessage = "Имя пользователя не должно быть пустым";
    }
}
