package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.model.BookedProducts;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookedProductsDto toBookedProductsDto(BookedProducts bookedProducts);
}
