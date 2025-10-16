package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DimensionDto {
    @DecimalMin(value = "1.0", message = "Минимальное значение ширины 0.0")
    @NotNull
    private Double width;
    @NotNull
    @DecimalMin(value = "1.0", message = "Минимальное значение высоты 0.0")
    private Double height;
    @NotNull
    @DecimalMin(value = "1.0", message = "Минимальное значение глубины 0.0")
    private Double depth;
}
