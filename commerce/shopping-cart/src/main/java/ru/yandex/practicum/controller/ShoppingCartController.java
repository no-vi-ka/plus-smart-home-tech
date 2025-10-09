package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;
import ru.yandex.practicum.dto.BookedProductDto;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private static final String USERNAME_PARAM = "username";

    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto getShoppingCart(
            @RequestParam(name = USERNAME_PARAM) String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto addProducts(
            @RequestParam(name = USERNAME_PARAM) String username,
            @RequestBody Map<UUID, Integer> products) {
        return shoppingCartService.addProducts(username, products);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateShoppingCart(
            @RequestParam(name = USERNAME_PARAM) String username) {
        shoppingCartService.deactivateShoppingCart(username);
    }

    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto removeProducts(
            @RequestParam(name = USERNAME_PARAM) String username,
            @RequestBody Map<UUID, Integer> products) {
        return shoppingCartService.removeProducts(username, products);
    }

    @PostMapping("/change-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCartDto changeProductQuantity(
            @RequestParam(name = USERNAME_PARAM) String username,
            @Valid @RequestBody ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }

    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public BookedProductDto bookProducts(
            @RequestParam(name = USERNAME_PARAM) String username) {
        return shoppingCartService.bookProducts(username);
    }
}