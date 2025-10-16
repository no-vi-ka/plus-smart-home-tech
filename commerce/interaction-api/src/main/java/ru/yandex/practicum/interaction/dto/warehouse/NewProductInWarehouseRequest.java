package ru.yandex.practicum.interaction.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Запрос на добавление нового товара на склад
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    // Запрос на добавление нового товара на склад
    @NotNull
    UUID productId;

    // Признак хрупкости
    Boolean fragile;

    // Размеры товара
    @NotNull
    DimensionDto dimension;

    // Вес товара
    @NotNull
    @Min(1)
    Double weight;
}
