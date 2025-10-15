package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("==> Get shopping cart for user {}", username);
        ShoppingCartDto result = shoppingCartService.getShoppingCart(username);
        log.info("<==> Shopping cart result = {}", result);

        return result;
    }

    @PutMapping
    public ShoppingCartDto addProductsToCart(@RequestParam String username,
                                             @RequestBody Map<UUID, Long> request) {
        log.info("==> Add products to shopping cart for user {}, request = {}", username, request);
        ShoppingCartDto result = shoppingCartService.addProductsToCart(username, request);
        log.info("<== Shopping cart result = {}", result);

        return result;
    }

    @DeleteMapping
    public void deactivateShoppingCart(@RequestParam String username) {
        log.info("==> Delete shopping cart for user {}", username);
        shoppingCartService.deactivateShoppingCart(username);
        log.info("<== Shopping cart is deleted");
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProducts(@RequestParam String username,
                                          @RequestBody List<UUID> request) {
        log.info("==> Remove products from shopping cart for user {}, request = {}", username, request);
        ShoppingCartDto result = shoppingCartService.removeProducts(username, request);
        log.info("<== Shopping cart after removing, result = {}", result);

        return result;
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantityShoppingCart(@RequestParam String username,
                                                      @RequestBody @Valid ChangeProductQuantityRequestDto request) {
        log.info("==> Change quantity on shopping cart for user {}, request = {}", username, request);
        ShoppingCartDto result = shoppingCartService.changeQuantityShoppingCart(username, request);
        log.info("<== Changed quantity on shopping cart result = {}", result);

        return result;
    }
}
