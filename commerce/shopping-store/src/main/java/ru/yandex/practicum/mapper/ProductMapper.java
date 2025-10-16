package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productId", ignore = true)
    Product toProduct(ProductDto productDto);

    ProductDto toProductDto(Product product);


    @Mapping(target = "productId", ignore = true)
    void updateEntityFromDto(ProductDto productDto, @MappingTarget Product product);
}
