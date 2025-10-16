package ru.practicum.service;

import ru.practicum.dto.*;

public interface WarehouseService {

    void newProductInWarehouse(NewProductInWarehouseRequest request);

    void addProductToWarehouse(AddProductToWarehouseRequest request);

    BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto);

    AddressDto getWarehouseAddress();
}
