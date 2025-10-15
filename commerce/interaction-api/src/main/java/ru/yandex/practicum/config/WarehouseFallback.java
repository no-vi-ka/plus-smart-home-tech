package ru.yandex.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;

@Configuration
@Slf4j
public class WarehouseFallback implements WarehouseClient {
    @Override
    public void createProduct(NewProductInWarehouseRequestDto product) {
        log.info("Warehouse Fallback response for createProduct: the service is temporarily unavailable");
    }

    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto cart) {
        log.info("Warehouse Fallback response for checkQuantity: the service is temporarily unavailable");
        return new BookedProductsDto();
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequestDto product) {
        log.info("Warehouse Fallback response for addProductToWarehouse: the service is temporarily unavailable");
    }

    @Override
    public AddressDto getAddress() {
        log.info("Warehouse Fallback response for getAddress: the service is temporarily unavailable");
        return new AddressDto();
    }
}
