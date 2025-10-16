package ru.yandex.practicum.interaction.client.feign.shopping.cart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.shopping.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ShoppingCartFallback implements ShoppingCartClientFeign {
    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCartFallbackException cause = new ShoppingCartFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public ShoppingCartDto addProductsToShoppingCart(Map<UUID, Integer> products, String username) {
        ShoppingCartFallbackException cause = new ShoppingCartFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public void deactivateShoppingCart(String username) {
        ShoppingCartFallbackException cause = new ShoppingCartFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public ShoppingCartDto removeProductsFromShoppingCart(List<UUID> productsIds, String username) {
        ShoppingCartFallbackException cause = new ShoppingCartFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public ShoppingCartDto changeProductsQuantityInShoppingCart(ChangeProductQuantityRequest request, String username) {
        ShoppingCartFallbackException cause = new ShoppingCartFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }
}
