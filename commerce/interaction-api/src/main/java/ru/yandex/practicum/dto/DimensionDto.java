package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DimensionDto {
    @Min(1)
    private Double width;

    @Min(1)
    private Double height;

    @Min(1)
    private Double depth;
}
