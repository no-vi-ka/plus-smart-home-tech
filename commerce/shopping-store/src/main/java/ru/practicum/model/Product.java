package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.ProductCategory;
import ru.practicum.dto.ProductState;
import ru.practicum.dto.QuantityState;

import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    UUID productId;
    @Column(name = "name")
    String productName;
    String description;
    String imageSrc;
    @Enumerated(value = EnumType.STRING)
    QuantityState quantityState;
    @Enumerated(value = EnumType.STRING)
    ProductState productState;
    Double rating;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "category")
    ProductCategory productCategory;
    Double price;
}
