package ru.yandex.practicum.shopping.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.shopping.cart.ShoppingCartApi;
import ru.yandex.practicum.interaction.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartApi {
    private final ShoppingCartService shoppingCartService;

    // Получить актуальную корзину для авторизованного пользователя.
    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("start получить корзину username={}", username);
        ShoppingCartDto result = shoppingCartService.getShoppingCart(username);
        log.info("success получить корзину username={}, result={}", username, result);
        return result;
    }

    // Добавить товар в корзину
    @Override
    public ShoppingCartDto addProductsToShoppingCart(
            Map<UUID, Integer> products, // Отображение идентификатора товара на отобранное количество
            String username) {
        log.info("start добавить товары в корзину username={}, products={}", username, products);
        ShoppingCartDto result = shoppingCartService.addProductsToShoppingCart(products, username);
        log.info("success добавить товары в корзину username={}, products={}, result={}", username, products, result);
        return result;
    }

    // Деактивация корзины товаров для пользователя
    @Override
    public void deactivateShoppingCart(String username) {
        log.info("start деактивировать корзину username={}", username);
        shoppingCartService.deactivateShoppingCart(username);
        log.info("success деактивировать корзину username={}", username);
    }

    // Удалить указанные товары из корзины пользователя
    @Override
    public ShoppingCartDto removeProductsFromShoppingCart(
            List<UUID> productsIds, // Список идентификаторов товаров, которые нужно удалить
            String username) {
        log.info("start удалить товары username={}, productsIds={}", username, productsIds);
        ShoppingCartDto result = shoppingCartService.removeProductsFromShoppingCart(productsIds, username);
        log.info("success удалить товары username={}, productsIds={}, result={}", username, productsIds, result);
        return result;
    }

    // Изменить количество товаров в корзине
    @Override
    public ShoppingCartDto changeProductsQuantityInShoppingCart(
            ChangeProductQuantityRequest request, // Отображение идентификатора товара на отобранное количество
            String username) {
        log.info("start изменить количество товаров username={}, request={}", username, request);
        ShoppingCartDto result = shoppingCartService.changeProductsQuantityInShoppingCart(request, username);
        log.info("success изменить количество товаров username={}, request={}, result={}", username, request, result);
        return result;
    }
}
