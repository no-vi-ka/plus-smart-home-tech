package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class AddProductToWarehouseRequest {
    @NotNull(message = "Укажите количество какого товара вы хотите увеличить")
    private UUID productId;
    @NotNull(message = "Количество добавочного товара должно быть указанно")
    @DecimalMin(value = "1", message = "Можно только увеличивать количество товаров")
    private Long quantity;
}
