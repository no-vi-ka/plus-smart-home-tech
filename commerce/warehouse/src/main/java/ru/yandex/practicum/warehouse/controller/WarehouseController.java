package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperation;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.contract.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.request.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.request.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.warehouse.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperation {
    private final WarehouseService service;

    @Override
    @Loggable
    @PutMapping
    public void setProductToWarehouse(@RequestBody @Valid @NotNull NewProductInWarehouseRequest request)
            throws SpecifiedProductAlreadyInWarehouseException {
        service.setProductToWarehouse(request);
    }

    @Override
    @Loggable
    @PostMapping("/check")
    public BookedProductsDto checkBookedProducts(@RequestBody @Valid @NotNull ShoppingCartDto cart)
            throws ProductInShoppingCartLowQuantityInWarehouse {
        return service.checkedBookedProducts(cart);
    }

    @Override
    @Loggable
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToWarehouse(@RequestBody @Valid @NotNull AddProductToWarehouseRequest request)
            throws NoSpecifiedProductInWarehouseException {
        service.addProductToWarehouse(request);
    }

    @Override
    @Loggable
    @GetMapping("/address")
    public AddressDto getAddress() {
        return service.getAddress();
    }
}
