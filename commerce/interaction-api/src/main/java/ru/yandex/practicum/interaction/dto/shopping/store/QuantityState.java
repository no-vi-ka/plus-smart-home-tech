package ru.yandex.practicum.interaction.dto.shopping.store;

/**
 * Статус, перечисляющий состояние остатка как свойства товара
 */

public enum QuantityState {
    ENDED,  // товар закончился
    FEW,    // осталось < 10 ед.
    ENOUGH, // осталось 10-100 ед.
    MANY    // осталось > 100 ед.
}
