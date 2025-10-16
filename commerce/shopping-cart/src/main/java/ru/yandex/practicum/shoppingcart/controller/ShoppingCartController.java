package ru.yandex.practicum.shoppingcart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperation;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.request.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.logging.Loggable;
import ru.yandex.practicum.shoppingcart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperation {
    public final ShoppingCartService service;

    @Override
    @GetMapping
    @Loggable
    public ShoppingCartDto getOrCreateShoppingCart(@RequestParam @NotEmpty String username) {
        return service.getOrCreateShoppingCart(username);
    }

    @Override
    @Loggable
    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull Map<@NotNull UUID, @NotNull @Positive Long> products
    ) {
        return service.addProductToShoppingCart(username, products);
    }

    @Override
    @Loggable
    @DeleteMapping
    public void deleteShoppingCart(@RequestParam @NotEmpty String username) {
        service.deactivateShoppingCart(username);
    }

    @Override
    @Loggable
    @PostMapping("/remove")
    public ShoppingCartDto removeProductFromShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull List<@NotNull UUID> productIds
    ) {
        return service.removeProductFromShoppingCart(username, productIds);
    }

    @Override
    @Loggable
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantityInShoppingCart(
            @RequestParam @NotEmpty String username,
            @RequestBody @Valid @NotNull ChangeProductQuantityRequest productQuantity
    ) {
        return service.changeProductQuantityInShoppingCart(username, productQuantity);
    }
}
