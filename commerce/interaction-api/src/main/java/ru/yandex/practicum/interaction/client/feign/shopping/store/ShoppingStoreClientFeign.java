package ru.yandex.practicum.interaction.client.feign.shopping.store;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.shopping.store.ShoppingStoreApi;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store", fallback = ShoppingStoreFallback.class)
public interface ShoppingStoreClientFeign extends ShoppingStoreApi {

}
