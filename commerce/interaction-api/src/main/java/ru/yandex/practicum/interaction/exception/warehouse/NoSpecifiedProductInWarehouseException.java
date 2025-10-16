package ru.yandex.practicum.interaction.exception.warehouse;

import ru.yandex.practicum.interaction.exception.BaseServiceException;

import java.util.List;
import java.util.UUID;

public class NoSpecifiedProductInWarehouseException extends BaseServiceException {
    public NoSpecifiedProductInWarehouseException(List<UUID> ids) {
        this.httpStatus = "400";
        this.userMessage = "Нет информации о товаре на складе ID:" + ids;
    }
}
