package ru.yandex.practicum.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private QuantityState quantityState;
}
