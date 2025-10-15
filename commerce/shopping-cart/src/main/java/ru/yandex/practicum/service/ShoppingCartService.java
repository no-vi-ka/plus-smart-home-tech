package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.ChangeProductQuantityRequestDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> request);

    void deactivateShoppingCart(String username);

    ShoppingCartDto removeProducts(String username, List<UUID> request);

    ShoppingCartDto changeQuantityShoppingCart(String username, ChangeProductQuantityRequestDto requestDto);
}
