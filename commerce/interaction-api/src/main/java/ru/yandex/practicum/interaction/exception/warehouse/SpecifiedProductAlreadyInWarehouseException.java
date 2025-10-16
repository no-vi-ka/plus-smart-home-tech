package ru.yandex.practicum.interaction.exception.warehouse;

import ru.yandex.practicum.interaction.exception.BaseServiceException;

public class SpecifiedProductAlreadyInWarehouseException extends BaseServiceException {
    public SpecifiedProductAlreadyInWarehouseException() {
        this.httpStatus = "400";
        this.userMessage = "Ошибка, товар с таким описанием уже зарегистрирован на складе";
    }
}
