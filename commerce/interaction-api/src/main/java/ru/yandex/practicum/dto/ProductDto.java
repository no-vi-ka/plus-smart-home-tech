package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
public class ProductDto {

    UUID productId;

    @NotBlank
    String productName;
    @NotBlank
    String description;

    String imageSrc;

    @NotNull
    SetProductQuantityStateRequest.QuantityState quantityState;

    @NotNull
    ProductState productState;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    Double rating;

    @NotNull
    private ProductCategory productCategory;

    @DecimalMin(value = "1.0")
    Double price;

    public enum ProductCategory {
        LIGHTING,
        CONTROL,
        SENSORS
    }

    public enum ProductState {
        ACTIVE,
        DEACTIVATE
    }
}
