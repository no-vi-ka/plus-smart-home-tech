package ru.yandex.practicum.commerce.contract.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.contract.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.request.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.request.warehouse.NewProductInWarehouseRequest;

public interface WarehouseOperation {
    @PutMapping
    void setProductToWarehouse(@RequestBody @Valid @NotNull NewProductInWarehouseRequest request)
            throws SpecifiedProductAlreadyInWarehouseException;

    @PostMapping("/check")
    BookedProductsDto checkBookedProducts(@RequestBody @Valid @NotNull ShoppingCartDto cart)
            throws ProductInShoppingCartLowQuantityInWarehouse;

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody @Valid @NotNull AddProductToWarehouseRequest request)
            throws NoSpecifiedProductInWarehouseException;

    @GetMapping("/address")
    AddressDto getAddress();
}
