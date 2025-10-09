package ru.yandex.practicum.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.shoppingStore.ShoppingStoreClient;
import ru.yandex.practicum.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found with ID: {}";
    private static final String BOOKING_NOT_FOUND_MESSAGE = "Booking not found with ID: {}";

    private final WarehouseProductRepository warehouseProductRepository;
    private final BookingRepository bookingRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    @Qualifier("mvcConversionService")
    private final ConversionService conversionService;

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        log.info("Adding new product to warehouse: {}", request);
        validateNewProductRequest(request);

        if (warehouseProductRepository.existsById(request.getProductId())) {
            throw new NoResultException("Product is already registered in warehouse");
        }

        ProductDto productDto = shoppingStoreClient.getProduct(request.getProductId());
        if (productDto == null) {
            throw new NoSuchElementException(String.format("Product with ID %s not found in store", request.getProductId()));
        }

        WarehouseProduct newProduct = conversionService.convert(request, WarehouseProduct.class);
        warehouseProductRepository.save(Objects.requireNonNull(newProduct));
        log.info("New product added successfully: {}", request.getProductId());
    }

    @Transactional
    public void acceptReturn(Map<UUID, Integer> products) {
        log.info("Accepting return of products: {}", products);
        validateProductsMap(products);

        products.forEach((productId, quantity) -> {
            WarehouseProduct product = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException(String.format(PRODUCT_NOT_FOUND_MESSAGE, productId)));
            product.setQuantityAvailable(product.getQuantityAvailable() + quantity);
            warehouseProductRepository.save(product);
            log.debug("Updated quantity for product {}: new quantity = {}", productId, product.getQuantityAvailable());
        });
        log.info("Return accepted successfully for products: {}", products.keySet());
    }

    @Transactional
    public BookedProductDto bookProductForShoppingCart(ShoppingCartDto shoppingCart) {
        log.info("Booking products for shopping cart: {}", shoppingCart);
        validateShoppingCart(shoppingCart);

        double totalWeight = 0;
        double totalVolume = 0;
        boolean fragile = false;

        for (Map.Entry<UUID, Integer> entry : shoppingCart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            int requestedQuantity = entry.getValue();

            WarehouseProduct product = warehouseProductRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException(String.format(PRODUCT_NOT_FOUND_MESSAGE, productId)));

            if (product.getQuantityAvailable() < requestedQuantity) {
                throw new NoSuchElementException(String.format("Insufficient quantity for product: %s", productId));
            }

            product.setQuantityAvailable(product.getQuantityAvailable() - requestedQuantity);
            warehouseProductRepository.save(product);

            updateProductQuantityState(productId, product.getQuantityAvailable());

            totalWeight += product.getWeight() * requestedQuantity;
            totalVolume += calculateVolume(product.getDimension()) * requestedQuantity;
            fragile |= product.isFragile();
        }

        Booking booking = Booking.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(shoppingCart.getProducts())
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();

        bookingRepository.save(booking);
        log.info("Order assembly completed for shopping cart ID: {}", shoppingCart.getShoppingCartId());

        return conversionService.convert(booking, BookedProductDto.class);
    }

    @Transactional(readOnly = true)
    public BookedProductDto assemblyProductForOrderFromShoppingCart(AssemblyProductForOrderFromShoppingCartRequest request) {
        log.info("Assembling order for shopping cart ID: {} and order ID: {}", request.getShoppingCartId(), request.getOrderId());
        validateAssemblyRequest(request);

        Booking booking = bookingRepository.findById(request.getShoppingCartId())
                .orElseThrow(() -> new NoSuchElementException(String.format(BOOKING_NOT_FOUND_MESSAGE, request.getShoppingCartId())));

        if (booking.getProducts() == null || booking.getProducts().isEmpty()) {
            throw new NoSuchElementException("Shopping cart is empty or not found");
        }

        log.info("Order assembly completed for shopping cart ID: {} and order ID: {}", request.getShoppingCartId(), request.getOrderId());
        return conversionService.convert(booking, BookedProductDto.class);
    }

    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        log.info("Adding quantity to product: {}", request);
        validateAddQuantityRequest(request);

        WarehouseProduct product = warehouseProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSuchElementException(String.format(PRODUCT_NOT_FOUND_MESSAGE, request.getProductId())));

        product.setQuantityAvailable(product.getQuantityAvailable() + request.getQuantity());
        WarehouseProduct updatedProduct = warehouseProductRepository.save(product);

        updateProductQuantityState(request.getProductId(), updatedProduct.getQuantityAvailable());
        log.info("Quantity updated for product: {}", request.getProductId());
    }

    public AddressDto getWarehouseAddress() {
        log.info("Retrieving warehouse address");
        return AddressDto.builder()
                .country("Country")
                .city("City")
                .street("Street")
                .house("1")
                .flat("1")
                .build();
    }

    private void updateProductQuantityState(UUID productId, int quantityAvailable) {
        SetProductQuantityStateRequest.QuantityState newState = SetProductQuantityStateRequest.QuantityState.determineState(quantityAvailable);
        shoppingStoreClient.setProductQuantityState(
                SetProductQuantityStateRequest.builder()
                        .productId(productId)
                        .quantityState(newState)
                        .build()
        );
    }

    private double calculateVolume(Dimension dimension) {
        return dimension.getWidth() * dimension.getHeight() * dimension.getDepth();
    }

    private void validateNewProductRequest(NewProductInWarehouseRequest request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("New product request or product ID cannot be null");
        }
    }

    private void validateProductsMap(Map<UUID, Integer> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Products map cannot be null or empty");
        }
        if (products.entrySet().stream().anyMatch(e -> e.getValue() <= 0)) {
            throw new IllegalArgumentException("Product quantity must be positive");
        }
    }

    private void validateShoppingCart(ShoppingCartDto shoppingCart) {
        if (shoppingCart == null || shoppingCart.getShoppingCartId() == null) {
            throw new IllegalArgumentException("Shopping cart or its ID cannot be null");
        }
        if (shoppingCart.getProducts() == null || shoppingCart.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Shopping cart products cannot be null or empty");
        }
    }

    private void validateAssemblyRequest(AssemblyProductForOrderFromShoppingCartRequest request) {
        if (request == null || request.getShoppingCartId() == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Assembly request, shopping cart ID, or order ID cannot be null");
        }
    }

    private void validateAddQuantityRequest(AddProductToWarehouseRequest request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("Add quantity request or product ID cannot be null");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }
    }
}