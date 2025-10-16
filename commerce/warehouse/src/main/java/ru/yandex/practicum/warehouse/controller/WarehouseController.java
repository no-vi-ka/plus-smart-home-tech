package ru.yandex.practicum.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.warehouse.WarehouseApi;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@Validated
@RequiredArgsConstructor
@Slf4j
public class WarehouseController implements WarehouseApi {
    private final WarehouseService warehouseService;

    // Добавить новый товар на склад.
    @Override
    public void newProduct(NewProductInWarehouseRequest newRequest) {
        log.info("start newProduct newRequest={}", newRequest);
        warehouseService.newProduct(newRequest);
        log.info("success newProduct newRequest={}", newRequest);
    }

    // Предварительно проверить что количество товаров на складе достаточно для данной корзины товаров.
    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto shoppingCartDto) {
        log.info("start checkProducts shoppingCartDto={}", shoppingCartDto);
        BookedProductsDto result = warehouseService.checkProducts(shoppingCartDto);
        log.info("success checkProducts shoppingCartDto={}, result={}", shoppingCartDto, result);
        return result;
    }

    // Принять товар на склад.
    @Override
    public void addProduct(AddProductToWarehouseRequest addRequest) {
        log.info("start addProduct addRequest={}", addRequest);
        warehouseService.addProduct(addRequest);
        log.info("success addProduct addRequest={}", addRequest);
    }

    // Предоставить адрес склада для расчёта доставки.
    @Override
    public AddressDto getAddress() {
        log.info("start getAddress");
        AddressDto result = warehouseService.getAddress();
        log.info("success getAddress result={}", result);
        return result;
    }
}
