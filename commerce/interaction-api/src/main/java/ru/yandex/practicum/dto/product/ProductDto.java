package ru.yandex.practicum.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
public class ProductDto {
    private UUID productId;
    @NotEmpty(message = "Название продукта не может быть пустым")
    private String productName;
    @NotEmpty(message = "Описание товара не может быть пустым")
    private String description;
    private String imageSrc;
    @NotNull(message = "Состояние остатка товара не должно быть пустое")
    private QuantityState quantityState;
    @NotNull(message = "Статус товара не может быть пустым")
    private ProductState productState;
    private ProductCategory productCategory;
    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "1.00", inclusive = true, message = "Цена не может быть меньше 1")
    private BigDecimal price;
}
