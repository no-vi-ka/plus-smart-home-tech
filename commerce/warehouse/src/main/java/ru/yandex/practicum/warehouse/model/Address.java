package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Представление адреса в БД
 */

// lombok annotations
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// JPA annotations
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID id;

    // Страна
    @Column(name = "country")
    String country;

    // Город
    @Column(name = "city")
    String city;

    // Улица
    @Column(name = "street")
    String street;

    // Дом
    @Column(name = "house")
    String house;

    // Квартира
    @Column(name = "flat")
    String flat;

    // Создаёт адрес заполняя специфичные поля указанным значением
    public static Address createTestAddress(String value) {
        return Address.builder()
                .country(value)
                .city(value)
                .street(value)
                .house(value)
                .flat(value)
                .build();
    }
}
