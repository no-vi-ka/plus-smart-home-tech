package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name = "warehouse_product")
@Getter
@Setter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    UUID productId;
    boolean fragile;
    Double width;
    Double height;
    Double depth;
    Double weight;
    long quantity = 0L;
}
