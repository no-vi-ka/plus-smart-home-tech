package ru.yandex.practicum.model;

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
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.enums.ProductState;
import ru.yandex.practicum.enums.QuantityState;

import java.util.UUID;

@Entity
@Table(name = "products")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    UUID productId;
    @Column(name = "product_name")
    String productName;
    @Column(name = "description")
    String description;
    @Column(name = "image_src")
    String imageSrc;
    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state")
    QuantityState quantityState;
    @Enumerated(EnumType.STRING)
    @Column(name = "product_state")
    ProductState productState;
    @Enumerated(EnumType.STRING)
    @Column(name = "product_category")
    ProductCategory productCategory;
    @Column(name = "price")
    Float price;
}