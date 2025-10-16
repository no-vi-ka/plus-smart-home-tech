package ru.yandex.practicum.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.feign.client.WarehouseClient;
import ru.yandex.practicum.exception.CartDeactivatedException;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.CartItemId;
import ru.yandex.practicum.repository.CartRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseClient client;

    @Transactional
    public ShoppingCartDto getCart(String username) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseGet(() -> createNewCartInTransaction(username));
        return mapper.toDto(cart);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cart createNewCartInTransaction(String username) {
        return repository.save(
                Cart.builder()
                        .items(new HashSet<>())
                        .username(username)
                        .build()
        );
    }

    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> newProducts) {
        if (newProducts == null || newProducts.isEmpty()) {
            throw new IllegalArgumentException("Список товаров для добавления не может быть пустым");
        }

        Cart cart = repository.findByUsernameWithItems(username)
                .orElseGet(() -> createCartForUser(username));

        if (!cart.isActive()) {
            throw new CartDeactivatedException(
                    "Корзина деактивирована",
                    "Корзина для пользователя " + username + " недоступна"
            );
        }

        ShoppingCartDto checkDto = mapper.toDto(cart);

        newProducts.forEach(checkDto::mergeProduct);

        client.checkProductState(checkDto);

        updateCartItems(cart, newProducts);
        cart = repository.save(cart);

        return mapper.toDto(cart);
    }

    private void updateCartItems(Cart cart, Map<UUID, Long> newProducts) {
        Map<UUID, CartItem> existingItems = cart.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getId().getProductId(),
                        Function.identity()
                ));

        newProducts.forEach((productId, quantity) -> {
            CartItem item = existingItems.get(productId);
            if (item != null) {
                item.setQuantity(item.getQuantity() + quantity);
            } else {
                CartItem newItem = createNewCartItem(cart, productId, quantity);
                cart.getItems().add(newItem);
            }
        });
    }


    private CartItem createNewCartItem(Cart cart, UUID productId, Long quantity) {
        CartItemId id = new CartItemId(cart.getCartId(), productId);
        return CartItem.builder()
                .id(id)
                .cart(cart)
                .quantity(quantity)
                .build();
    }

    @Transactional
    public void deleteCart(String username) {
        if (repository.deactivateCartByUsername(username) == 0){
            throw new EntityNotFoundException("Корзина не найдена для пользователя: " + username);
        }
    }

    @Transactional
    public ShoppingCartDto deleteProductFromCart(String username, List<UUID> productIds) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена для пользователя: " + username));

        if (!cart.isActive()) {
            throw new CartDeactivatedException("Корзина деактивирована", "Корзина для пользователя " + username + " недоступна");
        }

        List<CartItem> toRemove = cart.getItems().stream()
                .filter(item -> productIds.contains(item.getId().getProductId()))
                .toList();

        if (toRemove.size() != productIds.size()) {
            throw new NoProductsInShoppingCartException("Некоторые товары не найдены", "В корзине отсутствует часть указанных товаров");
        }

        toRemove.forEach(cart.getItems()::remove);

        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        Cart cart = repository.findByUsernameWithItems(username)
                .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена для пользователя: " + username));

        if (!cart.isActive()) {
            throw new CartDeactivatedException("Корзина деактивирована", "Корзина для пользователя " + username + " недоступна");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Товар не найден",
                        "Товара с Id " + request.getProductId() + " не найдено в корзине"
                ));
        cartItem.setQuantity(request.getNewQuantity());
        return mapper.toDto(cart);
    }

    private Cart createCartForUser(String username) {
        return repository.save(Cart.builder()
                .username(username)
                .active(true)
                .build());

    }
}
