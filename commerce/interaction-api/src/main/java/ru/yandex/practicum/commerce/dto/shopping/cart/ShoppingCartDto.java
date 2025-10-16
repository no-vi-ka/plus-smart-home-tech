package ru.yandex.practicum.commerce.dto.shopping.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShoppingCartDto {
    @NotNull
    private UUID id;

    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Long> products;
}
