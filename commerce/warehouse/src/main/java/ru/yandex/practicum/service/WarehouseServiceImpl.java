package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Override
    public void createProduct(NewProductInWarehouseRequestDto requestDto) {
        warehouseRepository.findById(requestDto.getProductId()).ifPresent(warehouse -> {
            throw new SpecifiedProductAlreadyInWarehouseException("The product already is in warehouse");
        });
        WarehouseProduct warehouseProduct = warehouseMapper.toWarehouseProduct(requestDto);
        warehouseRepository.save(warehouseProduct);
    }

    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) {
        BookedProductsDto BookedProductsDto = new BookedProductsDto();
        for (UUID productId : shoppingCartDto.getProducts().keySet()) {
            WarehouseProduct product = warehouseRepository.findByProductId(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("The product is not found in " +
                            "warehouse by productId =" + productId));
            long requestedQuantity = shoppingCartDto.getProducts().get(productId);
            long productQuantity = 0;
            if (product.getQuantity() != null) {
                productQuantity = product.getQuantity();
            }

            if (productQuantity < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("The product is not enough (" + productQuantity
                        + ") in warehouse for requested (" + requestedQuantity + ")");
            }

            double deliveryVolume = 0.0;
            if (BookedProductsDto.getDeliveryVolume() != null) {
                deliveryVolume = BookedProductsDto.getDeliveryVolume();
            }
            BookedProductsDto.setDeliveryVolume(
                    deliveryVolume + product.getDimension().getHeight()
                            * product.getDimension().getDepth()
                            * product.getDimension().getWidth()
            );

            double deliveryWeight = 0.0;
            if (BookedProductsDto.getDeliveryWeight() != null) {
                deliveryWeight = BookedProductsDto.getDeliveryWeight();
            }
            BookedProductsDto.setDeliveryWeight(deliveryWeight + product.getWeight());
            if (product.getFragile()) {
                BookedProductsDto.setFragile(true);
            }
            log.info("Successful checked productId = {}", productId);
        }
        log.info("All products have been checked");

        return BookedProductsDto;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequestDto requestDto) {
        WarehouseProduct product = warehouseRepository.findByProductId(requestDto.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("The product is not found in " +
                        "warehouse by productId =" + requestDto.getProductId()));

        long quantity = 0;
        if (product.getQuantity() != null) {
            quantity = product.getQuantity();
        }
        product.setQuantity(quantity + requestDto.getQuantity());
    }

    @Override
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country("country")
                .city("city")
                .street("street")
                .house("house")
                .flat("flat")
                .build();
    }
}
