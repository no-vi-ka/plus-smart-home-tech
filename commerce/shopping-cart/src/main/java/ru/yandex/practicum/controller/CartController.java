package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.contract.CartContract;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.service.CartService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class CartController implements CartContract {
    private final CartService cartService;

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam @NotBlank String username) {
        return cartService.getShoppingCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProduct(@RequestParam @NotBlank String username,
                                      @RequestBody @NotNull @NotEmpty Map<UUID, Integer> request) {
        return cartService.addProduct(username, request);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam @NotBlank String username) {
        cartService.deactivateCart(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeProduct(@RequestParam @NotBlank String username,
                                         @RequestBody @NotNull @NotEmpty Set<UUID> productsId) {
        return cartService.removeProduct(username, productsId);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto updateProductQuantity(@RequestParam @NotBlank String username,
                                                 @Valid @RequestBody ChangeProductQuantityRequest requestDto) {
        return cartService.updateProductQuantity(username, requestDto);
    }
}
