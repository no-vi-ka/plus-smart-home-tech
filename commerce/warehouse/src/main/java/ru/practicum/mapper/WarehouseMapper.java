package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.NewProductInWarehouseRequest;
import ru.practicum.model.WarehouseProduct;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {

    @Mapping(target = "width", source = "dto.dimension.width")
    @Mapping(target = "height", source = "dto.dimension.height")
    @Mapping(target = "depth", source = "dto.dimension.depth")
    @Mapping(target = "quantity", ignore = true)
    WarehouseProduct map(NewProductInWarehouseRequest dto);
}
