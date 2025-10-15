package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequestDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(ProductCategory category, Pageable pageable) {
        List<Product> products = productRepository.findAllByProductCategory(category, pageable);
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        } else {
            return productMapper.productsToProductDtos(products);
        }

    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.productDtoToProduct(productDto);
        return productMapper.productToProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product oldProduct = productRepository.findByProductId(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with id = " + productDto.getProductId() + " not found")
                );
        Product newProduct = productMapper.productDtoToProduct(productDto);
        newProduct.setProductId(oldProduct.getProductId());
        return productMapper.productToProductDto(productRepository.save(newProduct));
    }

    @Override
    public boolean removeProduct(UUID productId) {
        Product product = productRepository.findByProductId(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with id = " + productId + " not found")
        );
        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Override
    public boolean updateQuantityState(SetProductQuantityStateRequestDto setProductQuantityStateRequestDto) {
        Product product = productRepository.findByProductId(setProductQuantityStateRequestDto.getProductId())
                .orElseThrow(
                        () -> new ProductNotFoundException("Product with id = " +
                                setProductQuantityStateRequestDto.getProductId() + " not found")
                );
        product.setQuantityState(setProductQuantityStateRequestDto.getQuantityState());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        Product product = productRepository.findByProductId(productId).orElseThrow(
                () -> new ProductNotFoundException("Product with id = " + productId + " not found")
        );
        return productMapper.productToProductDto(product);
    }
}
