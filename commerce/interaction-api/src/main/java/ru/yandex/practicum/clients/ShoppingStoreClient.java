package ru.yandex.practicum.clients;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.requests.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping_store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam @NotNull ProductCategory category, Pageable pageable);

    @PutMapping
    public ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody @NotNull UUID productId);

    @PostMapping("/quantityState")
    public Boolean setProductQuantityState(@RequestBody @Valid SetProductQuantityStateRequest setProductQuantityStateRequest);

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable @NotNull UUID productId);

}
