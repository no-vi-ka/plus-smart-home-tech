package ru.yandex.practicum.interaction.dto.shopping.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Товар, продаваемый в интернет-магазине
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    // Идентификатор товара в БД
    UUID productId;

    // Наименование товара
    @NotNull
    String productName;

    // Описание товара
    @NotNull
    String description;

    // Ссылка на картинку во внешнем хранилище или SVG
    String imageSrc;

    // Статус, перечисляющий состояние остатка как свойства товара
    @NotNull
    QuantityState quantityState;

    // Статус товара
    @NotNull
    ProductState productState;

    // Категория товара
    ProductCategory productCategory;

    // Цена товара
    @NotNull
    @Min(1)
    BigDecimal price;
}
