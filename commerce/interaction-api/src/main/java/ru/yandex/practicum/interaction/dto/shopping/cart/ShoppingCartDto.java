package ru.yandex.practicum.interaction.dto.shopping.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

/**
 * Корзина товаров в онлайн магазине.
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCartDto {
    // Идентификатор корзины в БД
    @NotNull
    UUID shoppingCartId;

    // Отображение идентификатора товара на отобранное количество.
    @NotNull
    Map<UUID, @NotNull @Positive Integer> products;
}
