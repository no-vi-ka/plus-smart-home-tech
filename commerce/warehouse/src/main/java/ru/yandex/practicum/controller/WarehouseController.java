package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.feign.client.WarehouseClient;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {
    private final WarehouseService service;

    @PutMapping
    public void createProduct(@RequestBody @Valid NewProductInWarehouseRequest request){
            service.createProduct(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductState(@RequestBody @Valid ShoppingCartDto cartDto){
        return service.checkProductState(cartDto);
    }

    @PostMapping("/add")
    public  void addQuantityProductToWarehouse(@RequestBody AddProductToWarehouseRequest request){
        service.addQuantityProductToWarehouse(request);
    }

    @GetMapping("/address")
    public AddressDto getCurrentWarehouseAddress(){
        return service.getCurrentWarehouseAddress();
    }
}
