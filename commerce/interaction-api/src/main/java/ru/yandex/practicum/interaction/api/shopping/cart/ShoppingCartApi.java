package ru.yandex.practicum.interaction.api.shopping.cart;

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
import ru.yandex.practicum.interaction.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartApi {
    // Получить актуальную корзину для авторизованного пользователя.
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    // Добавить товар в корзину
    @PutMapping
    ShoppingCartDto addProductsToShoppingCart(
            @RequestBody @NotEmpty Map<UUID, @NotNull @Positive Integer> products, // Отображение идентификатора товара на отобранное количество
            @RequestParam String username);

    // Деактивация корзины товаров для пользователя
    @DeleteMapping
    void deactivateShoppingCart(@RequestParam String username);

    // Удалить указанные товары из корзины пользователя
    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromShoppingCart(
            @RequestBody @NotEmpty List<UUID> productsIds, // Список идентификаторов товаров, которые нужно удалить
            @RequestParam String username);

    // Изменить количество товаров в корзине
    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductsQuantityInShoppingCart(
            @Valid @RequestBody ChangeProductQuantityRequest request, // Отображение идентификатора товара на отобранное количество
            @RequestParam String username);
}
