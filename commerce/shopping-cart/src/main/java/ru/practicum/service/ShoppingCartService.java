package ru.practicum.service;

import jakarta.validation.constraints.NotNull;
import ru.practicum.dto.BookedProductsDto;
import ru.practicum.dto.ChangeProductQuantityRequest;
import ru.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getShoppingCart(String userName);

    ShoppingCartDto addProductsToShoppingCart(String userName, Map<UUID, @NotNull Long> products);

    ShoppingCartDto removeFromShoppingCart(String userName, List<UUID> products);

    ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest request);

    BookedProductsDto bookingProductsFromShoppingCart(String userName);

    void deleteShoppingCart(String userName);
}
