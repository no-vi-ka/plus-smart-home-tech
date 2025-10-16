package ru.yandex.practicum.interaction.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Запрос на увеличение единиц товара по его идентификатору
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductToWarehouseRequest {
    // Идентификатор товара в БД
    UUID productId;

    // Количество единиц товара для добавления на склад
    @NotNull
    @Min(1)
    Integer quantity;
}
