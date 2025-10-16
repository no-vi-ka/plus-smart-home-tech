package ru.yandex.practicum.shopping.cart.service;

import ru.yandex.practicum.interaction.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    // Получить актуальную корзину для авторизованного пользователя.
    ShoppingCartDto getShoppingCart(String username);

    // Добавить товар в корзину
    ShoppingCartDto addProductsToShoppingCart(
            Map<UUID, Integer> products, // Отображение идентификатора товара на отобранное количество
            String username);

    // Деактивация корзины товаров для пользователя
    void deactivateShoppingCart(String username);

    // Удалить указанные товары из корзины пользователя
    ShoppingCartDto removeProductsFromShoppingCart(
            List<UUID> productsIds, // Список идентификаторов товаров, которые нужно удалить
            String username);

    // Изменить количество товаров в корзине
    ShoppingCartDto changeProductsQuantityInShoppingCart(
            ChangeProductQuantityRequest request, // Отображение идентификатора товара на отобранное количество
            String username);
}
