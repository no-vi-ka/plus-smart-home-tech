package ru.yandex.practicum.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageableDto {

    private Integer page;

    @Min(1)
    private Integer size;

    private List<String> sort;
}
