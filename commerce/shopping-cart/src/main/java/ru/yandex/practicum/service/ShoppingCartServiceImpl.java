package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username);
        return shoppingCartMapper.toShoppingCartDto(cart);
    }

    @Override
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> request) {
        checkUsername(username);
        ShoppingCart cart = ShoppingCart.builder()
                .username(username)
                .products(request)
                .isActive(true)
                .build();

        return shoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(cart));
    }

    @Override
    public void deactivateShoppingCart(String username) {
        checkUsername(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username);
        cart.setIsActive(false);
    }

    @Override
    public ShoppingCartDto removeProducts(String username, List<UUID> request) {
        checkUsername(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username);
        if (cart == null) {
            throw new NoProductsInShoppingCartException("Shopping cart is empty for user " + username);
        }
        request.forEach(productId -> {
            cart.getProducts().remove(productId);
        });

        ShoppingCart updatedCart = shoppingCartRepository.save(cart);

        return shoppingCartMapper.toShoppingCartDto(updatedCart);
    }

    @Override
    public ShoppingCartDto changeQuantityShoppingCart(String username, ChangeProductQuantityRequestDto request) {
        checkUsername(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username);
        cart.getProducts().entrySet().stream()
                .filter(entry -> entry.getKey().equals(request.getProductId()))
                .peek(entry -> entry.setValue(request.getNewQuantity()))
                .findAny()
                .orElseThrow(() -> new NoProductsInShoppingCartException("Shopping cart is empty for user " + username));

        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toShoppingCartDto(cart);
        warehouseClient.checkQuantity(shoppingCartDto);

        shoppingCartRepository.save(cart);

        return shoppingCartDto;
    }

    private void checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Wrong username");
        }
    }
}
