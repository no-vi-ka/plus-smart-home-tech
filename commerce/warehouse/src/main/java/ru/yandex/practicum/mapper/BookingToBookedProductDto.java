package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.dto.BookedProductDto;

@Component
@RequiredArgsConstructor
public class BookingToBookedProductDto implements Converter<Booking, BookedProductDto> {

    @Override
    public BookedProductDto convert(Booking source) {
        validateBooking(source);

        return BookedProductDto.builder()
                .deliveryVolume(source.getDeliveryVolume())
                .deliveryWeight(source.getDeliveryWeight())
                .fragile(source.getFragile())
                .build();
    }

    private void validateBooking(Booking source) {
        if (source.getDeliveryVolume() <= 0) {
            throw new IllegalArgumentException("Delivery volume must be positive");
        }
        if (source.getDeliveryWeight() <= 0) {
            throw new IllegalArgumentException("Delivery weight must be positive");
        }
    }
}