package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ProductsDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequestDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.service.ProductService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@Slf4j
@RequiredArgsConstructor
public class ShoppingStoreController {
    private final ProductService productService;

    @GetMapping
    public ProductsDto getProductsByCategory(@RequestParam ProductCategory category,
                                             @Valid Pageable pageable) {
        log.info("==> Get products by category = {}, pageable = {}", category, pageable);
        ProductsDto result = ProductsDto.builder()
                .content(productService.getProductsByCategory(category, pageable))
                .sort(pageable.getSort())
                .build();
        log.info("<== Products by category result = {}", result);

        return result;
    }

    @PutMapping
    public ProductDto createProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("==> Create by productDto = {}", productDto);
        ProductDto result = productService.createProduct(productDto);
        log.info("<== Created result = {}", result);

        return result;
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("==> Update by productDto = {}", productDto);
        ProductDto result = productService.updateProduct(productDto);
        log.info("<== Updated result = {}", result);

        return result;
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody @NotNull UUID productId) {
        log.info("==> Remove product {}", productId);
        Boolean result = productService.removeProduct(productId);
        log.info("==> Remove result = {}", result);

        return result;
    }

    @PostMapping("/quantityState")
    public Boolean updateProductQuantityState(@RequestParam @NotNull String productId,
                                              @RequestParam @NotNull String quantityState) {
        log.info("==> Update quantity state productId = {}, quantityState = {}", productId, quantityState);
        SetProductQuantityStateRequestDto setProductQuantityStateRequestDto = new SetProductQuantityStateRequestDto();
        setProductQuantityStateRequestDto.setProductId(UUID.fromString(productId));

        QuantityState newState = QuantityState.FEW;
        if (quantityState != null && !quantityState.isEmpty()) {
            newState = QuantityState.valueOf(quantityState);
        }
        setProductQuantityStateRequestDto.setQuantityState(newState);

        Boolean result = productService.updateQuantityState(setProductQuantityStateRequestDto);
        log.info("<== Updated quantity state result = {}", result);

        return result;
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable("productId") @NotNull UUID productId) {
        log.info("==> Get product by id = {}", productId);
        ProductDto result = productService.getProduct(productId);
        log.info("<== Got product by id result = {}", result);

        return result;
    }
}
