package ru.yandex.practicum.commerce.contract.shopping.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.request.shopping.cart.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartOperation {
    @GetMapping
    ShoppingCartDto getOrCreateShoppingCart(@RequestParam @NotEmpty String username) throws NotAuthorizedUserException;

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull Map<@NotNull UUID, @NotNull @Positive Long> products
    ) throws NotAuthorizedUserException;

    @DeleteMapping
    void deleteShoppingCart(@RequestParam @NotEmpty String username) throws NotAuthorizedUserException;

    @PostMapping("/remove")
    ShoppingCartDto removeProductFromShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull List<@NotNull UUID> productIds
    ) throws NotAuthorizedUserException, NoProductsInShoppingCartException;

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantityInShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull ChangeProductQuantityRequest productQuantity
    ) throws NotAuthorizedUserException, NoProductsInShoppingCartException;
}
