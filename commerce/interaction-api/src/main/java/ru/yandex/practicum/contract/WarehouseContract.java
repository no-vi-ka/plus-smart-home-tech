package ru.yandex.practicum.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

public interface WarehouseContract {
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequestDto);

    BookedProductsDto checkProductQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    void updateProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest addProductToWarehouseRequestDto);

    AddressDto getAddress();
}