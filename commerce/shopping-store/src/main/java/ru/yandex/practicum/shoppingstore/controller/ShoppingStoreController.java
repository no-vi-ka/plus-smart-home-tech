package ru.yandex.practicum.shoppingstore.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperation;
import ru.yandex.practicum.commerce.contract.shopping.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.dto.shopping.store.PageableDto;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;
import ru.yandex.practicum.commerce.dto.shopping.store.QuantityState;
import ru.yandex.practicum.commerce.request.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.shoppingstore.dal.PageableMapper;
import ru.yandex.practicum.shoppingstore.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreOperation {
    private final ShoppingStoreService service;
    private final PageableMapper pageableMapper;

    @Override
    @GetMapping
    @Loggable
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        @RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "10") int size,
                                        @RequestParam(required = false) String[] sort) {
        PageableDto pageableDto = new PageableDto();
        pageableDto.setPage(page);
        pageableDto.setSize(size);
        pageableDto.setSort(sort != null ? sort : new String[]{"productName", "ASC"});

        return service.getProducts(category, pageableMapper.toSpringPageable(pageableDto));
    }

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Loggable
    public ProductDto addProduct(@RequestBody ProductDto productDto) {
        return service.addProduct(productDto);
    }

    @Override
    @PostMapping
    @Loggable
    public ProductDto updateProduct(@RequestBody ProductDto product) throws ProductNotFoundException {
        return service.updateProduct(product);
    }

    @Override
    @PostMapping("/removeProductFromStore")
    @Loggable
    public boolean deleteProduct(@RequestBody UUID productId) throws ProductNotFoundException {
        return service.deactivateProduct(productId);
    }

    @Override
    @PostMapping("/quantityState")
    @Loggable
    public boolean setProductQuantityState(@RequestParam @NotNull UUID productId,
                                           @RequestParam @NotNull QuantityState quantityState)
            throws ProductNotFoundException {
        SetProductQuantityStateRequest newQuantity = new SetProductQuantityStateRequest();
        newQuantity.setProductId(productId);
        newQuantity.setQuantityState(quantityState);
        return service.setProductQuantityState(newQuantity);
    }

    @Override
    @GetMapping("/{productId}")
    @Loggable
    public ProductDto getProduct(@PathVariable UUID productId) throws ProductNotFoundException {
        return service.getProduct(productId);
    }
}