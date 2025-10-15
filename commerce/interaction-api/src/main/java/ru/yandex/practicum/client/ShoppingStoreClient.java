package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {
    @GetMapping
    List<ProductDto> getProductsByCategory(@RequestParam ProductCategory category, Pageable pageable);

    @PutMapping
    ProductDto createProduct(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    Boolean updateProductQuantityState(@RequestParam UUID productId, @RequestParam String quantityState);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
