package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class NewProductInWarehouseRequest {
    @NotNull(message = "Товар должен иметь id")
    private UUID productId;
    private Boolean fragile = false;
    @NotNull(message = "Товар должен иметь размеры")
    private DimensionDto dimension;
    @NotNull(message = "Товар должен иметь вес")
    private Double weight;

}
