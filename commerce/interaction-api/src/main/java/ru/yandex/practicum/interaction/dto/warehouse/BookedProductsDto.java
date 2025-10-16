package ru.yandex.practicum.interaction.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Общие сведения о зарезервированных товарах по корзине.
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {
    // Общий вес доставки
    @NotNull
    Double deliveryWeight;

    // Общий объём доставки
    @NotNull
    Double deliveryVolume;

    // Есть ли хрупкие вещи в доставке
    @NotNull
    boolean fragile;
}
