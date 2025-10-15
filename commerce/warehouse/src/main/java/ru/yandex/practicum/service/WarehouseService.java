package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

public interface WarehouseService {
    void createProduct(NewProductInWarehouseRequestDto requestDto);

    BookedProductsDto checkQuantity(ShoppingCartDto dto);

    AddressDto getAddress();

    void addProductToWarehouse(AddProductToWarehouseRequestDto requestDto);
}
