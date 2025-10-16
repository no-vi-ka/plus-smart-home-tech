package ru.yandex.practicum.interaction.client.feign.shopping.cart;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.shopping.cart.ShoppingCartApi;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart", fallback = ShoppingCartFallback.class)
public interface ShoppingCartClientFeign extends ShoppingCartApi {

}
