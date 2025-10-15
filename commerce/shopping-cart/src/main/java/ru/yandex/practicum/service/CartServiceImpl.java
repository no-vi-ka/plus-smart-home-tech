package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.enums.CartState;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart cart = getOrCreateCart(username);
        return CartMapper.mapToCartDto(cart);
    }

    @Override
    public ShoppingCartDto addProduct(String username, Map<UUID, Integer> products) {
        ShoppingCart cart = getOrCreateCart(username);
        cartRepository.save(cart);

        mergeProducts(cart.getProducts(), products);

        checkWarehouseAvailability(cart);

        cart = cartRepository.save(cart);

        ShoppingCartDto result = CartMapper.mapToCartDto(cart);
        log.debug("Товары добавлены в корзину пользователя {}. Итоговое количество товаров: {}",
                username, result.getProducts().size());
        return result;
    }

    @Override
    public void deactivateCart(String username) {
        ShoppingCart cart = cartRepository.findByUsername(username).orElseThrow(
                () -> new NotFoundException(String.format("Корзина для пользователя %s не найдена", username))
        );

        if (cart.getCartState() != CartState.DEACTIVATE) {
            cart.setCartState(CartState.DEACTIVATE);
            cartRepository.save(cart);
            log.info("Корзина пользователя {} деактивирована", username);
        } else {
            log.debug("Корзина уже деактивирована для пользователя: {}", username);
        }
    }

    @Override
    public ShoppingCartDto removeProduct(String username, Set<UUID> productIds) {
        ShoppingCart cart = checkShoppingCart(username);

        if (cart.getProducts() != null) {
            cart.getProducts().keySet().removeAll(productIds);
        }

        return CartMapper.mapToCartDto(cartRepository.save(cart));
    }

    @Override
    public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequest requestDto) {
        ShoppingCart cart = checkShoppingCart(username);

        Map<UUID, Integer> products = cart.getProducts();
        if (products == null) {
            products = new HashMap<>();
            cart.setProducts(products);
        }

        UUID productId = requestDto.getProductId();
        Integer newQuantity = requestDto.getNewQuantity();

        if (!products.containsKey(productId)) {
            throw new NoProductsInShoppingCartException(String.format("Товар с ID %s отсутствует в корзине", productId));
        }

        if (newQuantity == 0) {
            products.remove(productId);
        } else {
            products.put(productId, newQuantity);
        }

        return CartMapper.mapToCartDto(cartRepository.save(cart));
    }

    private ShoppingCart checkShoppingCart(String username) {
        return cartRepository.findByUsernameAndCartState(username, CartState.ACTIVE).orElseThrow(
                () -> new NotFoundException(
                        String.format("Активной корзины покупок для пользователя %s не найдено", username)
                )
        );
    }

    private ShoppingCart getOrCreateCart(String username) {
        return cartRepository.findByUsername(username)
                .orElseGet(() -> {
                    log.debug("Корзина для пользователя {} не найдена. Создаю новую.", username);
                    return ShoppingCart.builder()
                            .username(username)
                            .cartState(CartState.ACTIVE)
                            .products(new HashMap<>())
                            .build();
                });
    }

    private void mergeProducts(Map<UUID, Integer> existingProducts, Map<UUID, Integer> newProducts) {
        if (existingProducts == null) {
            throw new IllegalStateException("Существующие продукты не могут быть null");
        }
        newProducts.forEach((productId, quantity) ->
                existingProducts.merge(productId, quantity, Integer::sum));
    }

    private void checkWarehouseAvailability(ShoppingCart cart) {
        try {
            log.info("Проверка наличия товаров на складе: id={}, products={}",
                    cart.getShoppingCartId(), cart.getProducts());

            BookedProductsDto bookedProducts = warehouseClient
                    .checkProductQuantity(CartMapper.mapToCartDto(cart));

            log.info("Проверено наличие на складе: {}", bookedProducts);
        } catch (FeignException e) {
            log.error("Ошибка вызова склада: {}", e.getMessage());
            throw new RuntimeException("Склад недоступен", e);
        }
    }
}
