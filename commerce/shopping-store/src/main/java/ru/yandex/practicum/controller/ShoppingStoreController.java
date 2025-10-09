package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private static final String CATEGORY_PARAM = "category";
    private static final String PAGEABLE_PARAM = "pageableDto";
    private static final String PRODUCT_ID_PARAM = "productId";

    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDto> getProducts(
            @RequestParam(name = CATEGORY_PARAM) ProductDto.ProductCategory category,
            @Valid @RequestParam(name = PAGEABLE_PARAM) PageableDto pageableDto) {
        return shoppingStoreService.getProductsByCategory(category, pageableDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createNewProduct(
            @Valid @RequestBody ProductDto productDto) {
        return shoppingStoreService.createNewProduct(productDto);
    }

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto)  {
        return shoppingStoreService.updateProduct(productDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProductFromStore(
            @RequestParam("PRODUCT_ID_PARAM") UUID productId) {
        shoppingStoreService.removeProductFromStore(productId);
    }

    @PostMapping("/quantity-state")
    @ResponseStatus(HttpStatus.OK)
    public void setProductQuantityState(
            @Valid @RequestBody SetProductQuantityStateRequest request) {
        shoppingStoreService.setProductQuantityState(request);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto getProduct(
            @PathVariable(PRODUCT_ID_PARAM) UUID productId) {
        return shoppingStoreService.getProduct(productId);
    }
}