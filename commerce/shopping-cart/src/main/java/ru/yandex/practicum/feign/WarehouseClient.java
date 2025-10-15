package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.contract.WarehouseContract;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient extends WarehouseContract {

    @Override
    @PutMapping
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequest newProductInWarehouseRequest);

    @Override
    @PostMapping("/check")
    BookedProductsDto checkProductQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @Override
    @PostMapping("/add")
    void updateProductQuantity(@RequestBody AddProductToWarehouseRequest addProductToWarehouseRequest);

    @Override
    @GetMapping("/address")
    AddressDto getAddress();
}
