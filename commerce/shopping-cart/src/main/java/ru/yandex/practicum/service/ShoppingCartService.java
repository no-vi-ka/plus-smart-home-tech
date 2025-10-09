package ru.yandex.practicum.service;

import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.dto.BookedProductDto;
import ru.yandex.practicum.warehouse.WarehouseClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {

    private static final String CART_NOT_FOUND_MESSAGE = "Shopping cart not found for user: {}";
    private static final String EMPTY_USERNAME_MESSAGE = "Username cannot be empty";

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;
    @Qualifier("mvcConversionService")
    private final ConversionService conversionService;

    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Retrieving shopping cart for user: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseGet(() -> createNewShoppingCart(username));

        return convertToDto(shoppingCart, "successfully retrieved");
    }

    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Integer> products) {
        log.info("Adding products to shopping cart for user: {}", username);
        validateUsername(username);
        validateProducts(products);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        updateProducts(shoppingCart, products, true);

        return saveAndConvert(shoppingCart, "successfully updated");
    }

    @Transactional
    public void deactivateShoppingCart(String username) {
        log.info("Deactivating shopping cart for user: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        shoppingCart.setActive(false);
        shoppingCartRepository.save(shoppingCart);
        log.info("Shopping cart for user {} deactivated", username);
    }

    @Transactional
    public ShoppingCartDto removeProducts(String username, Map<UUID, Integer> products) {
        log.info("Removing products from shopping cart for user: {}", username);
        validateUsername(username);
        validateProducts(products);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        updateProducts(shoppingCart, products, false);

        return saveAndConvert(shoppingCart, "successfully updated after removal");
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Changing product quantity in shopping cart for user: {}", username);
        validateUsername(username);
        validateRequest(request);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        UUID productId = request.getProductId();
        int newQuantity = request.getNewQuantity();

        Map<UUID, Integer> products = shoppingCart.getProducts();
        if (!products.containsKey(productId)) {
            throw new NoResultException("Product not found in cart: " + productId);
        }

        if (newQuantity <= 0) {
            products.remove(productId);
        } else {
            products.put(productId, newQuantity);
        }

        return saveAndConvert(shoppingCart, String.format(
                "successfully updated, product %s quantity changed to %d", productId, newQuantity));
    }

    @Transactional
    public BookedProductDto bookProducts(String username) {
        log.info("Booking products for user: {}", username);
        validateUsername(username);

        ShoppingCart shoppingCart = getActiveShoppingCart(username);
        Map<UUID, Integer> products = shoppingCart.getProducts();

        if (products.isEmpty()) {
            throw new NoResultException("Shopping cart is empty for user: " + username);
        }

        ShoppingCartDto cartDto = convertToDto(shoppingCart, "ready for booking");
        BookedProductDto bookedProducts = warehouseClient.bookProducts(cartDto);

        shoppingCart.setActive(false);
        shoppingCartRepository.save(shoppingCart);

        log.info("Products successfully booked for user: {}", username);
        return bookedProducts;
    }

    private ShoppingCart getActiveShoppingCart(String username) {
        return shoppingCartRepository.findByUsernameAndActive(username, true)
                .orElseThrow(() -> new NoResultException(
                        String.format(CART_NOT_FOUND_MESSAGE, username)));
    }

    private void updateProducts(ShoppingCart shoppingCart, Map<UUID, Integer> products, boolean isAdding) {
        products.forEach((productId, quantity) ->
                shoppingCart.getProducts().merge(productId, isAdding ? quantity : -quantity,
                        (current, change) -> {
                            int newQuantity = current + change;
                            return newQuantity > 0 ? newQuantity : null;
                        }));
    }

    private ShoppingCartDto saveAndConvert(ShoppingCart shoppingCart, String successMessage) {
        shoppingCartRepository.save(shoppingCart);
        return convertToDto(shoppingCart, successMessage);
    }

    private ShoppingCartDto convertToDto(ShoppingCart shoppingCart, String successMessage) {
        ShoppingCartDto dto = conversionService.convert(shoppingCart, ShoppingCartDto.class);
        log.info("Shopping cart for user {} {}: {}", shoppingCart.getUsername(), successMessage, dto);
        return dto;
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(EMPTY_USERNAME_MESSAGE);
        }
    }

    private void validateProducts(Map<UUID, Integer> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Product list cannot be empty");
        }
        if (products.entrySet().stream().anyMatch(e -> e.getValue() <= 0)) {
            throw new IllegalArgumentException("Product quantity must be positive");
        }
    }

    private void validateRequest(ChangeProductQuantityRequest request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("Change quantity request cannot be empty");
        }
    }

    private ShoppingCart createNewShoppingCart(String username) {
        log.info("Creating new shopping cart for user: {}", username);
        ShoppingCart cart = ShoppingCart.builder()
                .username(username)
                .active(true)
                .products(new HashMap<>())
                .build();
        return shoppingCartRepository.save(cart);
    }
}