package ru.yandex.practicum.interaction.client.feign.shopping.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductDto;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductPageDto;
import ru.yandex.practicum.interaction.dto.shopping.store.QuantityState;

import java.util.UUID;

@Slf4j
@Component
public class ShoppingStoreFallback implements ShoppingStoreClientFeign {

    @Override
    public ProductPageDto getProductsByCategory(ProductCategory category, Pageable pageable) {
        ShoppingStoreFallbackException cause = new ShoppingStoreFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        ShoppingStoreFallbackException cause = new ShoppingStoreFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        ShoppingStoreFallbackException cause = new ShoppingStoreFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public Boolean deleteProduct(UUID productId) {
        ShoppingStoreFallbackException cause = new ShoppingStoreFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public Boolean setProductQuantityState(@RequestParam UUID productId,
                                           @RequestParam QuantityState quantityState) {
        ShoppingStoreFallbackException cause = new ShoppingStoreFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        ShoppingStoreFallbackException cause = new ShoppingStoreFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }
}
