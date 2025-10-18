package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.clients.ShoppingStoreClient;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.requests.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ShoppingStoreServiceImpl;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ShoppingStoreServiceImpl service;

    @Override
    public List<ProductDto> getProducts(@RequestParam @NotNull ProductCategory productCategory,
                                         Pageable pageable) {
        log.info("Get list of products in pageable view");
        return service.getProducts(productCategory, pageable);
    }

    @Override
    public ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Create new product");
        return service.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Update product with id = {}", productDto.getProductId());
        return service.updateProduct(productDto);
    }

    @Override
    public Boolean removeProduct(@RequestBody @NotNull UUID productId) {
        log.info("Remove product with id = {}", productId);
        return service.removeProductFromStore(productId);
    }

    @Override
    public Boolean setProductQuantityState(@RequestBody @Valid
                                               SetProductQuantityStateRequest setProductQuantityStateRequest) {
        log.info("Set product with id = {} quantity state", setProductQuantityStateRequest.getProductId());
        return service.setProductQuantityState(setProductQuantityStateRequest);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable @NotNull UUID productId) {
        log.info("Get product with id = {}", productId);
        return service.getProduct(productId);
    }

}
