package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.dto.PageableDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingStoreService {

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found with ID: {}";

    private final ProductRepository productRepository;
    @Qualifier("mvcConversionService")
    private final ConversionService conversionService;

    public List<ProductDto> getProductsByCategory(ProductDto.ProductCategory category, PageableDto pageableDto) {
        validatePageable(pageableDto);
        Pageable pageable = convertToPageable(pageableDto);
        List<Product> products = productRepository.findAllByProductCategory(category, pageable).getContent();

        return products.stream()
                .map(product -> conversionService.convert(product, ProductDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        validateProductDto(productDto, true);
        Product product = conversionService.convert(productDto, Product.class);
        Product savedProduct = productRepository.save(Objects.requireNonNull(product));
        return conversionService.convert(savedProduct, ProductDto.class);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        validateProductId(productId);
        validateProductDto(productDto, false);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(String.format(PRODUCT_NOT_FOUND_MESSAGE, productId)));
        updateProductFields(product, productDto);
        Product updatedProduct = productRepository.save(product);
        return conversionService.convert(updatedProduct, ProductDto.class);
    }

    @Transactional
    public void removeProductFromStore(UUID productId) {
        validateProductId(productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(String.format(PRODUCT_NOT_FOUND_MESSAGE, productId)));
        productRepository.delete(product);
    }

    @Transactional
    public void setProductQuantityState(SetProductQuantityStateRequest request) {
        validateQuantityStateRequest(request);
        UUID productId = request.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(String.format(PRODUCT_NOT_FOUND_MESSAGE, productId)));

        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
    }

    public ProductDto getProduct(UUID productId) {
        validateProductId(productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(String.format(PRODUCT_NOT_FOUND_MESSAGE, productId)));
        return conversionService.convert(product, ProductDto.class);
    }

    private void updateProductFields(Product product, ProductDto productDto) {
        if (productDto.getProductName() != null) {
            product.setProductName(productDto.getProductName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getImageSrc() != null) {
            product.setImageSrc(productDto.getImageSrc());
        }
        if (productDto.getQuantityState() != null) {
            product.setQuantityState(productDto.getQuantityState());
        }
        if (productDto.getProductState() != null) {
            product.setProductState(productDto.getProductState());
        }
        if (productDto.getRating() > 0) {
            product.setRating(productDto.getRating());
        }
        if (productDto.getProductCategory() != null) {
            product.setProductCategory(productDto.getProductCategory());
        }
        if (productDto.getPrice() != null && productDto.getPrice() >= 1) {
            product.setPrice(productDto.getPrice());
        }
    }

    private Pageable convertToPageable(PageableDto pageableDto) {
        if (pageableDto.getSort() == null || pageableDto.getSort().isEmpty()) {
            return PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
        }

        Sort sort = Sort.by(
                pageableDto.getSort().stream()
                        .map(sortStr -> {
                            String[] sortParams = sortStr.split(",");
                            if (sortParams.length == 2 && "desc".equalsIgnoreCase(sortParams[1])) {
                                return Sort.Order.desc(sortParams[0]);
                            }
                            return Sort.Order.asc(sortParams[0]);
                        })
                        .collect(Collectors.toList()));
        return PageRequest.of(pageableDto.getPage(), pageableDto.getSize(), sort);
    }

    private void validateProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
    }

    private void validateProductDto(ProductDto productDto, boolean isNew) {
        if (productDto == null) {
            throw new IllegalArgumentException("Product DTO cannot be null");
        }
        if (isNew && productDto.getProductName() == null) {
            throw new IllegalArgumentException("Product name is required for new products");
        }
    }

    private void validatePageable(PageableDto pageableDto) {
        if (pageableDto == null) {
            throw new IllegalArgumentException("Pageable DTO cannot be null");
        }
        if (pageableDto.getPage() < 0 || pageableDto.getSize() <= 0) {
            throw new IllegalArgumentException("Page must be non-negative and size must be positive");
        }
    }

    private void validateQuantityStateRequest(SetProductQuantityStateRequest request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("Quantity state request or product ID cannot be null");
        }
    }
}