package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.QuantityState;
import ru.yandex.practicum.dto.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreController {
    private final ProductService productService;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        Pageable pageable) {
        log.info("Получение продуктов по категории {}, с настройками по странице {}", category, pageable);
        return productService.getProductPagaeble(category, pageable);
    }

    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Запрос на создание продукта: {}", productDto);
        return productService.createProduct(productDto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Запрос на обновление продукта: {}", productDto);
        return productService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUID productId) {
        log.info("Запрос на удаление продукта: {}", productId);
        return productService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public boolean changeQuantityState(@RequestParam UUID productId,
                                       @RequestParam QuantityState quantityState) {
        SetProductQuantityStateRequest productDto = new SetProductQuantityStateRequest(productId, quantityState);
        log.info("Установка нового значение состояния количесва продуктов в магазине {}", productDto);
        return productService.changeQuantityState(productDto);
    }


    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("Получение информация о прод куте по id: {}", productId);
        return productService.getProductById(productId);
    }


}
