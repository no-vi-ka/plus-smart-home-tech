package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductsToCart(@RequestParam String username, @RequestBody Map<UUID, Integer> dto);

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto changeShoppingCart(@RequestParam String username, @RequestBody Map<UUID, Integer> dto);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantityShoppingCart(@RequestParam String username,
                                               @RequestBody ChangeProductQuantityRequestDto request);
}
