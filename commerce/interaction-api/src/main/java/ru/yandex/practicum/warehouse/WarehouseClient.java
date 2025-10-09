package ru.yandex.practicum.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.dto.BookedProductDto;

@Component
@FeignClient(name = "warehouse-service", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PostMapping("/booking")
    BookedProductDto bookProducts(@RequestBody ShoppingCartDto shoppingCart);

    @PostMapping("/assembly")
    BookedProductDto assembleOrder(@RequestBody AssemblyProductForOrderFromShoppingCartRequest request);
}