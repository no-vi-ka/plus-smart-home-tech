package ru.yandex.practicum.commerce.contract.shopping.store;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.contract.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.QuantityState;

import java.util.UUID;

public interface ShoppingStoreOperation {
    @GetMapping
    Page<ProductDto> getProducts(
            @RequestParam @NotNull ProductCategory category,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size,
            @RequestParam(required = false) String[] sort
    );

    @PutMapping
    ProductDto addProduct(@RequestBody @NotNull @Valid ProductDto product);

    @PostMapping
    ProductDto updateProduct(@RequestBody @NotNull @Valid ProductDto product) throws ProductNotFoundException;

    @PostMapping("/removeProductFromStore")
    boolean deleteProduct(@RequestBody @NotNull UUID productId) throws ProductNotFoundException;

    @PostMapping("/quantityState")
    boolean setProductQuantityState(@RequestParam @NotNull UUID productId,
                                    @RequestParam @NotNull QuantityState quantityState)
            throws ProductNotFoundException;

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable @NotNull UUID productId) throws ProductNotFoundException;
}
