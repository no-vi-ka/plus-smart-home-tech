package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.WarehouseContract;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseContract {
    private final WarehouseService warehouseService;

    @Override
    @PutMapping
    public void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequestDto) {
        warehouseService.addProduct(newProductInWarehouseRequestDto);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantity(shoppingCartDto);
    }

    @Override
    @PostMapping("/add")
    public void updateProductQuantity(@RequestBody @Valid AddProductToWarehouseRequest addProductToWarehouseRequestDto) {
        warehouseService.updateProductQuantity(addProductToWarehouseRequestDto);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }
}
