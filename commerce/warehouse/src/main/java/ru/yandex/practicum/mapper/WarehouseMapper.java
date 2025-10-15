package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);
}
