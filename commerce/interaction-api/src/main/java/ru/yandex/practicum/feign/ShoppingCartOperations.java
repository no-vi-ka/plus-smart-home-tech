package ru.yandex.practicum.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartOperations {

    @GetMapping
    ShoppingCartDto getUsersShoppingCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(@RequestParam String username,
                                             @RequestBody Map<UUID, Integer> products);

    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductFromShoppingCart(@RequestParam String username,
                                                  @RequestBody List<UUID> products);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantityInCart(@RequestParam String username,
                                                @Valid @RequestBody ChangeProductQuantityRequest request);
}
