package ru.yandex.practicum.shopping.cart.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// JPA annotations
@Entity
@Table(name = "shopping_cart",
        indexes = @Index(name = "idx_username", columnList = "username")) // поиск корзины часто идёт по username
// lombok annotations
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShoppingCart {
    // Идентификатор корзины в БД
    @Column(name = "shopping_cart_id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID shoppingCartId;

    // имя пользователя
    @Column(name = "username", nullable = false, unique = true)
    String username;

    // статус корзины
    @Enumerated
    @Column(name = "status")
    @Builder.Default
    ShoppingCartStatus status = ShoppingCartStatus.ACTIVE;


    // отображение идентификатора товара на отобранное количество
    @ElementCollection
    @CollectionTable(
            name = "cart_product",
            joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @Builder.Default
    Map<UUID, Integer> products = new HashMap<>();
}
