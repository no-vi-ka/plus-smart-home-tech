package ru.yandex.practicum.commerce.dto.shopping.store;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageableDto {
    @Min(0)
    private int page;

    @Min(1)
    private int size;
    private String[] sort; // [property, direction]
}
