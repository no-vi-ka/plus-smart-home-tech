package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.DimensionDto;
import ru.yandex.practicum.model.Warehouse;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

public class WarehouseMapper {

    public static Warehouse mapFromRequest(NewProductInWarehouseRequest request) {
        return Warehouse.builder()
                .productId(request.getProductId())
                .fragile(request.getFragile())
                .dimensionDto(DimensionDto.builder()
                        .width(request.getDimension().getWidth())
                        .height(request.getDimension().getHeight())
                        .depth(request.getDimension().getDepth())
                        .build())
                .weight(request.getWeight())
                .build();
    }

}
