package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.config.WarehouseFallback;
import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseFallback.class)
public interface WarehouseClient {

    @PutMapping
    void createProduct(@RequestBody NewProductInWarehouseRequestDto product);

    @PostMapping("/check")
    BookedProductsDto checkQuantity(@RequestBody ShoppingCartDto cart);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody AddProductToWarehouseRequestDto product);

    @GetMapping("/address")
    AddressDto getAddress();
}