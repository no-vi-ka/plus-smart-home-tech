package ru.yandex.practicum.shopping.store.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductDto;
import ru.yandex.practicum.shopping.store.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    ProductDto toProductDto(Product product);

    Product toProduct(ProductDto productDto);

    /**
     * Частично обновляет product используя productDto, null поля игнорируются
     *
     * @param product     - target объект, будет обновлен
     * @param productDto  - source объект, содержит данные для обновления
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(@MappingTarget Product product, ProductDto productDto);
}
