package ru.yandex.practicum.controller;


import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ShoppingCartDto getCart(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username) {
        log.info("Запрос пользователя на получение корзины с username {}", username);
        return cartService.getCart(username);
    }

    @PutMapping
    public ShoppingCartDto addProduct(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
                                      @RequestBody Map<UUID, Long> newProduct) {
        log.info("Запрос пользователя с username {} на добавление продуктов в корзину {}", username, newProduct.toString());
        return cartService.addProducts(username, newProduct);
    }


    @DeleteMapping
    public void deleteCart(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username) {
        log.info("Запрос пользователя на удаление корзины c Username: {}", username);
        cartService.deleteCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto deleteProductFromCart(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
                                      @RequestBody List<UUID> productIds) {
        log.info("Запрос на удаление продуктов из корзины пользователя : {}, список id продуктов {}",
                username, productIds.toString());
        return cartService.deleteProductFromCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam @NotBlank(message = "Имя пользователя на должно быть пустым") String username,
                                                 @RequestBody ChangeProductQuantityRequest productQuantityRequest) {
        log.info("Запрос пользователя с username: {} , на изменение количества продуктов {}", username, productQuantityRequest);
        return cartService.changeProductQuantity(username, productQuantityRequest);

    }
}
