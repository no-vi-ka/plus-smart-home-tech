package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BookedProductsDto {
    @NotNull(message = "У забронированных вещей быть указан вес")
    private Double deliveryWight;
    @NotNull(message = "У забронированных вещей должен быть указан обЪем")
    private Double deliveryVolume;
    @NotNull(message = "У забронированных вещей должно быть указанно наличие хрупких товаров")
    private Boolean fragile;
}
