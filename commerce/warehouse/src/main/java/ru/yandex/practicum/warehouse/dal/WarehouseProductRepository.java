package ru.yandex.practicum.warehouse.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {
    Optional<WarehouseProduct> findByProductId(UUID productId);
}
