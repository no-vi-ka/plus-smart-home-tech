package ru.yandex.practicum.interaction.exception.warehouse;

import ru.yandex.practicum.interaction.util.ProductNotEnough;
import ru.yandex.practicum.interaction.exception.BaseServiceException;

import java.util.List;

public class ProductInShoppingCartLowQuantityInWarehouseException extends BaseServiceException {

    public ProductInShoppingCartLowQuantityInWarehouseException(List<ProductNotEnough> productsNotEnough) {
        this.httpStatus = "400";
        this.userMessage = "Ошибка, товар из корзины не находится в требуемом количестве на складе: "
                           + productsNotEnough;
    }
}
