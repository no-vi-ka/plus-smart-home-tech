package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.dto.DimensionDto;

@Component
@RequiredArgsConstructor
public class DimensionDtoToDimension implements Converter<DimensionDto, Dimension> {

    @Override
    public Dimension convert(DimensionDto source) {
        validateDimensions(source);

        return Dimension.builder()
                .depth(source.getDepth())
                .height(source.getHeight())
                .width(source.getWidth())
                .build();
    }

    private void validateDimensions(DimensionDto source) {
        if (source.getDepth() <= 0) {
            throw new IllegalArgumentException("Depth must be positive");
        }
        if (source.getHeight() <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        if (source.getWidth() <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
    }
}