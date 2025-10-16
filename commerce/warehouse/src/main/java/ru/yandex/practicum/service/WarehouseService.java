package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];


    @Transactional
    public void createProduct(@Valid NewProductInWarehouseRequest request) {
        repository.findById(request.getProductId())
                .ifPresent(p -> {
                    throw new SpecifiedProductAlreadyInWarehouseException(
                            "Товар уже создан", "Товар с id " + request.getProductId() + " уже создан");
                });

        repository.save(mapper.toEntity(request));
    }

    @Transactional
    public BookedProductsDto checkProductState(@Valid ShoppingCartDto cartDto) {
        Set<UUID> productIds = cartDto.getProducts().keySet();
        List<WarehouseProduct> products = repository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Не все товары найдены на складе",
                    "Некоторые товары отсутствуют в БД склада"
            );
        }

        double totalWeight = 0;
        double totalVolume = 0;
        boolean hasFragile = false;

        for (WarehouseProduct product : products) {
            long requiredQty = cartDto.getProducts().get(product.getProductId());

            if (product.getQuantity() < requiredQty) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара",
                        "Товара с id " + product.getProductId() + " доступно " + product.getQuantity()
                );
            }

            totalWeight += product.getWeight() * requiredQty;
            totalVolume += product.getWidth() * product.getHeight() * product.getDepth() * requiredQty;

            if (product.isFragile()) {
                hasFragile = true;
            }
        }

        BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWight(totalWeight);
        dto.setDeliveryVolume(totalVolume);
        dto.setFragile(hasFragile);

        return dto;
    }


    @Transactional
    public void addQuantityProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар не найден",
                        "Товар с Id" + request.getProductId() + " не найден"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
    }

    public AddressDto getCurrentWarehouseAddress() {
        AddressDto dto = new AddressDto();
        dto.setCountry(CURRENT_ADDRESS);
        dto.setCity(CURRENT_ADDRESS);
        dto.setStreet(CURRENT_ADDRESS);
        dto.setHouse(CURRENT_ADDRESS);
        dto.setFlat(CURRENT_ADDRESS);
        return dto;
    }
}
