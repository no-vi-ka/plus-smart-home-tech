package ru.yandex.practicum.shopping.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductDto;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductPageDto;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductState;
import ru.yandex.practicum.interaction.dto.shopping.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.exception.shopping.store.ProductNotFoundException;
import ru.yandex.practicum.shopping.store.mapper.ProductMapper;
import ru.yandex.practicum.shopping.store.model.Product;
import ru.yandex.practicum.shopping.store.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductPageDto getProductsByCategory(ProductCategory category, Pageable pageable) {
        log.trace("start getProductsByCategory category={} pageable={}", category, pageable);

        // поиск только всех товаров
        List<Product> products = productRepository.findByProductCategory(category, pageable);

        List<ProductDto> productsDto = products.stream()
                .map(productMapper::toProductDto)
                .toList();

        ProductPageDto result = new ProductPageDto(productsDto, pageable.getSort());

        log.trace("success getProductsByCategory category={} pageable={} result={}",
                category,
                pageable,
                result);

        return result;
    }

    @Transactional
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.trace("start createProduct productDto={}", productDto);

        Product product = productMapper.toProduct(productDto);
        productRepository.save(product);
        log.trace("success create productId={}", product.getProductId());

        return productMapper.toProductDto(product);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.trace("start updateProduct productDto={}", productDto);

        // проверка на наличие в БД
        UUID productId = productDto.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        // обновление
        productMapper.updateProductFromDto(product, productDto);
        log.trace("success updateProduct productId={}", product.getProductId());

        return productMapper.toProductDto(product);
    }

    @Transactional
    @Override
    public Boolean deleteProduct(UUID productId) {
        log.trace("start deleteProduct productId={}", productId);

        // проверка на наличие в БД
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        // если уже DEACTIVATE, вернуть false
        if (product.getProductState().equals(ProductState.DEACTIVATE)) {
            return false;
        }

        product.setProductState(ProductState.DEACTIVATE);

        log.trace("success deleteProduct productId={}", productId);
        return true;
    }

    @Transactional
    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.trace("start setProductQuantityState request={}", request);

        // проверка на наличие в БД
        UUID productId = request.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        // установка количества
        product.setQuantityState(request.getQuantityState());

        log.trace("success setProductQuantityState request={}", request);
        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.trace("start getProduct productId={}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        log.trace("success getProduct productId={}", productId);
        return productMapper.toProductDto(product);
    }
}
