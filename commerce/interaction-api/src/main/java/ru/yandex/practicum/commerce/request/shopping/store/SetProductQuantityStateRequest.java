package ru.yandex.practicum.commerce.request.shopping.store;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.commerce.dto.shopping.store.QuantityState;

import java.util.UUID;

@Data
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}
