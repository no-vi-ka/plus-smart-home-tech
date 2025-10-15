package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {
    UUID productId;
    @NotBlank
    @Size(max = 255, message = "Максимальная длина наименования товара - 255 символов.")
    String productName;
    @NotBlank
    @Size(max = 255, message = "Максимальная длина описания товара - 255 символов.")
    String description;
    @Size(max = 255, message = "Максимальная длина ссылки на картинку - 255 символов.")
    String imageSrc;
    @NotNull
    QuantityState quantityState;
    @NotNull
    ProductState productState;
    ProductCategory productCategory;
    @NotNull
    @Min(1)
    @Positive(message = "Цена товара должна быть положительным числом.")
    Float price;
}
