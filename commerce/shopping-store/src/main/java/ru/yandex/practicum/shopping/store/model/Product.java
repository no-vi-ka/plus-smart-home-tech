package ru.yandex.practicum.shopping.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ru.yandex.practicum.interaction.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductState;
import ru.yandex.practicum.interaction.dto.shopping.store.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Товар, продаваемый в интернет-магазине
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    UUID productId;

    // Наименование товара
    @Column(name = "product_name")
    String productName;

    // Описание товара
    @Column(name = "description")
    String description;

    // Ссылка на картинку во внешнем хранилище или SVG
    @Column(name = "image_src")
    String imageSrc;

    // Статус, перечисляющий состояние остатка как свойства товара
    @Column(name = "quantity_state")
    @Enumerated(EnumType.STRING)
    QuantityState quantityState;

    // Статус товара
    @Column(name = "product_state")
    @Enumerated(EnumType.STRING)
    ProductState productState;

    // Категория товара
    @Column(name = "product_category")
    @Enumerated(EnumType.STRING)
    ProductCategory productCategory;

    // Цена товара
    @Column(name = "price", precision = 19, scale = 2)
    BigDecimal price;
}
