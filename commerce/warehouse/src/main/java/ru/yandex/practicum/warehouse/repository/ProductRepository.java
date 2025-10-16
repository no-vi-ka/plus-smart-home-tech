package ru.yandex.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.model.Product;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Поиск товаров по списку id и их представление в виде мапы
     *
     * @param ids - список id товаров для поиска
     * @return - Отображение идентификатора товара на сам товар (key - product.id, value - product).
     */
    default Map<UUID, Product> findAllAsMapByIds(Iterable<UUID> ids) {
        return findAllById(ids).stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));
    }
}
