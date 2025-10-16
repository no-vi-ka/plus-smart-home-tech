package ru.yandex.practicum.commerce.client.shopping.store;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperation;

@FeignClient(name = "shopping-store",
        path = "/api/v1/shopping-store",
        configuration = FeignConfig.class)
public interface ShoppingStoreClient extends ShoppingStoreOperation {
}
