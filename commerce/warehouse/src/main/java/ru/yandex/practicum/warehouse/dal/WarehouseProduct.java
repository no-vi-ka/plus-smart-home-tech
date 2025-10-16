package ru.yandex.practicum.warehouse.dal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "fragile")
    private boolean fragile;

    @Column(name = "width")
    private double width;

    @Column(name = "height")
    private double height;

    @Column(name = "depth")
    private double depth;

    @Column(name = "weight")
    private double weight;

    @Column(name = "quantity")
    private long quantity;
}
