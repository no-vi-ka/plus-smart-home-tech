package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;
import ru.yandex.practicum.requests.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ShoppingStoreRepository shoppingStoreRepository;
    private final ProductMapper mapper;

    @Override
    public List<ProductDto> getProducts(ProductCategory productCategory, Pageable pageable) {
        List<Product> products = shoppingStoreRepository.findAllByProductCategory(productCategory, pageable);
        return mapper.productsToProductsDto(products);
    }

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product newProduct = mapper.productDtoToProduct(productDto);
        return mapper.productToProductDto(shoppingStoreRepository.save(newProduct));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product savedProduct = shoppingStoreRepository.findByProductId(productDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with id = %s not found", productDto.getProductId()))
                );
        Product newProduct = mapper.productDtoToProduct(productDto);
        newProduct.setProductId(productDto.getProductId());
        return mapper.productToProductDto(shoppingStoreRepository.save(newProduct));
    }

    @Override
    @Transactional
    public Boolean removeProductFromStore(UUID productId) {
        Product product = shoppingStoreRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with id = %s not found", productId))
                );
        product.setProductState(ProductState.DEACTIVATE);
        shoppingStoreRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        Product product = shoppingStoreRepository.findByProductId(setProductQuantityStateRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with id = %s not found", setProductQuantityStateRequest.getProductId()))
                );
        product.setQuantityState(setProductQuantityStateRequest.getQuantityState());
        shoppingStoreRepository.save(product);
        return true;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        Product product = shoppingStoreRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with id = %s not found", productId))
                );
        return mapper.productToProductDto(product);
    }
}
