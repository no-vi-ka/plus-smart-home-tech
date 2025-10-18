package ru.yandex.practicum.clients;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.requests.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping_cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {
    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam @NotNull String username);

    @PutMapping
    public ShoppingCartDto addProductToShoppingCart(@RequestParam @NotNull String username,
                                                    @RequestBody Map<UUID, Integer> products);

    @DeleteMapping
    public void deactivateCurrentShoppingCart(@RequestParam(name = "username") @NotNull String username);

    @PostMapping("/remove")
    public ShoppingCartDto removeFromShoppingCart(@RequestParam(name = "username") @NotNull String username,
                                                  @RequestBody List<UUID> products);

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam(name = "username") @NotNull String username,
                                                 @RequestBody @Valid ChangeProductQuantityRequest request);
}
