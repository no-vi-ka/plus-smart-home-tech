package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.ProductState;
import ru.yandex.practicum.dto.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductNotFoundException;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    public Page<ProductDto> getProductPagaeble(ProductCategory category, Pageable pageable) {
        return repository.findByProductCategory(category, pageable).map(mapper::toProductDto);
    }

    @Transactional
    public ProductDto createProduct(@Valid ProductDto productDto) {
        return mapper.toProductDto(repository.save(mapper.toProduct(productDto)));

    }

    @Transactional
    public ProductDto updateProduct(@Valid ProductDto productDto) {
        Product product = repository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id" + productDto.getProductId() + " не найдено",
                        "Product not found", new RuntimeException("Underlying cause")));
        mapper.updateEntityFromDto(productDto, product);
        return mapper.toProductDto(product);
    }

    @Transactional
    public boolean removeProduct(UUID productId) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id" + productId + " не найдено",
                        "Product not found", new RuntimeException("Underlying cause")));
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Transactional
    public boolean changeQuantityState(@Valid SetProductQuantityStateRequest productDto) {
        Product product = repository.findById(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id" + productDto.getProductId() + " не найдено",
                        "Product not found", new RuntimeException("Underlying cause")));
        product.setQuantityState(productDto.getQuantityState());
        return true;
    }

    public ProductDto getProductById(UUID productId) {
        return mapper.toProductDto(repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id" + productId + " не найдено",
                        "Product not found", new RuntimeException("Underlying cause"))));
    }
}
