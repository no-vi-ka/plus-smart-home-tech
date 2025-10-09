package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDto {
    @DecimalMin(value = "1")
    private double width;
    @DecimalMin(value = "1")
    private double height;
    @DecimalMin(value = "1")
    private double depth;
}
