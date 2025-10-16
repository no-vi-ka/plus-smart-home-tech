package ru.yandex.practicum.commerce.request.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.commerce.dto.warehouse.DimensionDto;

import java.util.UUID;

@Data
public class NewProductInWarehouseRequest {
    @NotNull
    private UUID productId;
    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Min(1)
    private Double weight;
}
