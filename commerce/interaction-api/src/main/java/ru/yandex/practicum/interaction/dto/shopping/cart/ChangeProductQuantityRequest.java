package ru.yandex.practicum.interaction.dto.shopping.cart;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Запрос на изменение количества единиц товара
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeProductQuantityRequest {
    // Идентификатор товара
    @NotNull
    UUID productId;

    // Новое количество товара
    @NotNull
    Integer newQuantity;
}
