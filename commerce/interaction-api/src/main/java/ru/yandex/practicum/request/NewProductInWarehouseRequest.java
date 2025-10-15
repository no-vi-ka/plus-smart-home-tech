package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.dto.DimensionDto;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    @NotNull
    UUID productId;

    Boolean fragile;

    @NotNull
    DimensionDto dimension;

    @Positive
    Double weight;
}