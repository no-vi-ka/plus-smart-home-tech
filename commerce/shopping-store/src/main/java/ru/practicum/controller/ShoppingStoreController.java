package ru.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ShoppingStoreClient;
import ru.practicum.dto.*;
import ru.practicum.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    @GetMapping
    public List<ProductDto> getProducts(@NotNull ProductCategory category, Pageable pageable) {
        return shoppingStoreService.getProducts(category, pageable);
    }

    @Override
    public ProductDto getProduct(@PathVariable UUID productId) {
        return shoppingStoreService.getProductById(productId);
    }

    @Override
    public ProductDto createNewProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.addProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        return shoppingStoreService.deleteProduct(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return shoppingStoreService.setQuantityState(request);
    }
}
