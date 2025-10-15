package ru.yandex.practicum.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.request.ChangeProductQuantityRequest;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartContract {
    ShoppingCartDto getShoppingCart(@RequestParam @NotBlank String username);

    ShoppingCartDto addProduct(@RequestParam @NotBlank String username,
                               @RequestBody @NotNull @NotEmpty Map<UUID, Integer> products);

    void deactivateCart(@RequestParam @NotBlank String username);

    ShoppingCartDto removeProduct(@RequestParam @NotBlank String username,
                                  @RequestBody @NotNull @NotEmpty Set<UUID> products);

    ShoppingCartDto updateProductQuantity(@RequestParam @NotBlank String username,
                                          @RequestBody @Valid ChangeProductQuantityRequest request);
}