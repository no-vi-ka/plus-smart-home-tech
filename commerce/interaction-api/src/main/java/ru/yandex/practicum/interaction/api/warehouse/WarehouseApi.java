package ru.yandex.practicum.interaction.api.warehouse;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseApi {
    // Добавить новый товар на склад.
    @PutMapping
    void newProduct(@Valid @RequestBody NewProductInWarehouseRequest newRequest);

    // Предварительно проверить что количество товаров на складе достаточно для данной корзины товаров.
    @PostMapping("/check")
    BookedProductsDto checkProducts(@Valid @RequestBody ShoppingCartDto shoppingCartDto);

    // Принять товар на склад.
    @PostMapping("/add")
    void addProduct(@Valid @RequestBody AddProductToWarehouseRequest addRequest);

    // Предоставить адрес склада для расчёта доставки.
    @GetMapping("/address")
    AddressDto getAddress();
}
