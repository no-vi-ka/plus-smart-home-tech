package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.dto.NewProductInWarehouseRequest;

@Component
@RequiredArgsConstructor
public class NewProductInWarehouseRequestToWarehouseProduct implements Converter<NewProductInWarehouseRequest, WarehouseProduct> {

    @Qualifier("mvcConversionService")
    private final ConversionService conversionService;

    @Override
    public WarehouseProduct convert(NewProductInWarehouseRequest source) {

        return WarehouseProduct.builder()
                .productId(source.getProductId())
                .dimension(convertDimension(source.getDimension()))
                .weight(source.getWeight())
                .fragile(source.isFragile())
                .quantityAvailable(0)
                .build();
    }

    private Dimension convertDimension(Object dimensionDto) {
        if (dimensionDto == null) {
            throw new IllegalArgumentException("Dimension cannot be null");
        }
        return conversionService.convert(dimensionDto, Dimension.class);
    }
}