package ru.yandex.practicum.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimensionDto {
    @DecimalMin(value = "1.0")
    double width;

    @DecimalMin(value = "1.0")
    double height;

    @DecimalMin(value = "1.0")
    double depth;
}