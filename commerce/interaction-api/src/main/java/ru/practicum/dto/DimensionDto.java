package ru.practicum.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DimensionDto {
    @NotNull
    @Min(value = 1)
    Double width;
    @NotNull
    @Min(value = 1)
    Double height;
    @NotNull
    @Min(value = 1)
    Double depth;
}
