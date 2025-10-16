package ru.yandex.practicum.interaction.client.feign.warehouse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;

@Slf4j
@Component
public class WarehouseClientFallback implements WarehouseClientFeign {

    @Override
    public void newProduct(NewProductInWarehouseRequest newRequest) {
        WarehouseFallbackException cause = new WarehouseFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto shoppingCartDto) {
        WarehouseFallbackException cause = new WarehouseFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public void addProduct(AddProductToWarehouseRequest addRequest) {
        WarehouseFallbackException cause = new WarehouseFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }

    @Override
    public AddressDto getAddress() {
        WarehouseFallbackException cause = new WarehouseFallbackException();
        log.error(cause.getMessage(), cause);
        throw cause;
    }
}
