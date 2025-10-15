package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.StoreContract;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient extends StoreContract {

    @Override
    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @Override
    @PutMapping
    ProductDto createProduct(@RequestBody @Valid ProductDto newProductDto);

    @Override
    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto updateProductDto);

    @Override
    @PostMapping("/removeProductFromStore")
    Boolean removeProduct(@RequestBody @NotNull UUID productId);

    @Override
    @PostMapping("/quantityState")
    Boolean updateQuantityState(@RequestParam @NotNull UUID productId,
                                @RequestParam @NotNull QuantityState quantityState);

    @Override
    @GetMapping("/{product-id}")
    ProductDto getProductById(@PathVariable @NotNull UUID productId);
}
