package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.*;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Warehouse;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.request.NewProductInWarehouseRequest;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ShoppingStoreClient shoppingStoreClient;

    @Override
    public void addProduct(NewProductInWarehouseRequest requestDto) {
        UUID productId = requestDto.getProductId();

        if (warehouseRepository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    String.format("Товар с ID = %s уже заведен на склад", productId));
        }

        Warehouse product = WarehouseMapper.mapFromRequest(requestDto);
        product.setQuantity(0);
        warehouseRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantity(ShoppingCartDto cartDto) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Integer> entry : cartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            long requestedQty = entry.getValue();

            Warehouse product = warehouseRepository.findById(productId).orElseThrow(
                    () -> new NotFoundException(String.format("Продукт id = %s не найден на складе", productId))
            );

            if (product.getQuantity() < requestedQty) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        String.format("На складе недостаточно товара id = %s", productId));
            }

            totalWeight += product.getWeight() * requestedQty;
            totalVolume += product.getDimensionDto().getWidth() *
                    product.getDimensionDto().getHeight() *
                    product.getDimensionDto().getDepth() * requestedQty;

            if (product.getFragile()) hasFragile = true;
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    @Override
    public AddressDto getAddress() {
        String address = Address.CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    @Override
    public void updateProductQuantity(AddProductToWarehouseRequest requestDto) {
        UUID productId = requestDto.getProductId();
        Warehouse product = warehouseRepository.findById(productId).orElseThrow(
                () -> new NotFoundException(String.format("Продукт id = %s не найден на складе", productId))
        );
        product.setQuantity(product.getQuantity() + requestDto.getQuantity());
        warehouseRepository.save(product);
    }
}
