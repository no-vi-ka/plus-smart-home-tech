package ru.yandex.practicum.commerce.request.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddProductToWarehouseRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private Long quantity;
}
