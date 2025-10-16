package ru.yandex.practicum.commerce.client.shopping.cart;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperation;

@FeignClient(name = "shopping-cart",
        path = "/api/v1/shopping-cart",
        configuration = FeignConfig.class)
public interface ShoppingCartClient extends ShoppingCartOperation {
}
