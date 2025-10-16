package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
 * Товар, хранящийся на складе в БД
 */

// JPA annotations
@Entity
@Table(name = "product")
// Lombok annotations
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    // Идентификатор товара в БД
    @Id
    @Column(name = "product_id")
    UUID productId;

    // Признак хрупкости
    @Column(name = "fragile")
    Boolean fragile;

    // Размеры товара
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dimension_id", referencedColumnName = "id")
    Dimension dimension;

    // Вес товара
    @Column(name = "weight")
    Double weight;

    // Количество товара на складе
    @Column(name = "quantity")
    @Builder.Default
    Integer quantity = 0;
}
