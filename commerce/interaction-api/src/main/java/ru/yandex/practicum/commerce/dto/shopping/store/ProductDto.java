package ru.yandex.practicum.commerce.dto.shopping.store;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDto {
    private UUID productId;

    @NotEmpty
    private String productName;

    @NotEmpty
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    private ProductCategory productCategory;

    @NotNull
    private BigDecimal price;
}
