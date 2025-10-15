package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.dto.SetProductQuantityStateRequestDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProduct(UUID productId);

    boolean updateQuantityState(SetProductQuantityStateRequestDto setProductQuantityStateRequestDto);

    ProductDto getProduct(UUID productId);
}
