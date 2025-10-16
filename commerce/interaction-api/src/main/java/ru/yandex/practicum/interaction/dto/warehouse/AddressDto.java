package ru.yandex.practicum.interaction.dto.warehouse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Представление адреса в системе.
 */

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {
    // Страна
    String country;

    // Город
    String city;

    // Улица
    String street;

    // Дом
    String house;

    // Квартира
    String flat;
}
