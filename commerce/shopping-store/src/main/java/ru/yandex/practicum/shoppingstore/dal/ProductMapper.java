package ru.yandex.practicum.shoppingstore.dal;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.commerce.dto.shopping.store.ProductDto;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {
    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);

    void updateEntityFromDto(ProductDto dto, @MappingTarget Product entity);
}
