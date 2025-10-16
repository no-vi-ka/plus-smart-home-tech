package ru.yandex.practicum.interaction.exception.shopping.store;

import ru.yandex.practicum.interaction.exception.BaseServiceException;

public class ProductNotFoundException extends BaseServiceException {
    public ProductNotFoundException() {
        this.httpStatus = "404";
        this.userMessage = "Ошибка, товар по идентификатору в БД не найден";
    }
}
