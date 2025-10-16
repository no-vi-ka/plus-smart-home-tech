package ru.yandex.practicum.interaction.dto.shopping.store;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@Builder
public class ProductPageDto {

    public ProductPageDto(List<ProductDto> content, Sort sort) {
        this.content = content;
        this.sort = sort;
    }

    List<ProductDto> content;

    Sort sort;
}
