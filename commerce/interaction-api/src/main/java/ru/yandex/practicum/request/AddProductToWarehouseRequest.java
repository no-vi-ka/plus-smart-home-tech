package ru.yandex.practicum.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductToWarehouseRequest {
    @NotNull
    UUID productId;

    @NotNull
    Integer quantity;
}
