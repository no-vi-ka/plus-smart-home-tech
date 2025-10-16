package ru.yandex.practicum.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShoppingCartDto {
    private UUID shoppingCartId;
    private Map<UUID, Long> products = new HashMap<>();


    public ShoppingCartDto(UUID shoppingCartId, Map<UUID, Long> products) {
        this.shoppingCartId = shoppingCartId;
        this.products = products != null ? new HashMap<>(products) : new HashMap<>();
    }


    public UUID getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(UUID shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }


    public Map<UUID, Long> getProducts() {
        return new HashMap<>(products);
    }


    public void setProducts(Map<UUID, Long> products) {
        this.products = products != null ? new HashMap<>(products) : new HashMap<>();
    }


    public void mergeProduct(UUID productId, Long quantity) {
        products.merge(productId, quantity, Long::sum);
    }
}
