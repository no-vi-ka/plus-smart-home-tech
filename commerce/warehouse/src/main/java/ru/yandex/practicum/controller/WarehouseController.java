package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@Slf4j
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    public void createProduct(@RequestBody @Valid NewProductInWarehouseRequestDto requestDto) {
        log.info("==> Create product requestDto = {}", requestDto);
        warehouseService.createProduct(requestDto);
    }

    @PostMapping("/check")
    public BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto cart) {
        log.info("==> Check product quantity in shopping card = {}", cart);
        BookedProductsDto result = warehouseService.checkQuantity(cart);
        log.info("<== Result of check = {}", result);

        return result;
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequestDto requestDto) {
        log.info("==> Add product to warehouse, requestDto = {}", requestDto);
        warehouseService.addProductToWarehouse(requestDto);
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        log.info("==> Get address");
        AddressDto result = warehouseService.getAddress();
        log.info("<== Got address result = {}", result);

        return result;
    }
}