package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.clients.ShoppingStoreClient;
import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.BookedProductsDto;
import ru.yandex.practicum.dto.ProductDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.requests.AddProductToWarehouseRequest;
import ru.yandex.practicum.requests.NewProductInWarehouseRequest;
import ru.yandex.practicum.requests.SetProductQuantityStateRequest;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;
    private final ShoppingStoreClient shoppingStoreClient;


    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        repository.findById(request.getProductId()).ifPresent(
                product -> {
                    throw new SpecifiedProductAlreadyInWarehouseException("Product is already in warehouse");
                }
        );
        repository.save(mapper.toWarehouseProduct(request));
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        Map<UUID, Integer> products = cartDto.getProducts();
        List<WarehouseProduct> availableProducts = repository.findAllById(products.keySet());
        Map<UUID, WarehouseProduct> availableProductsMap = availableProducts.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        for (Map.Entry<UUID, Integer> product : products.entrySet()) {
            UUID id = product.getKey();
            WarehouseProduct availableProduct = availableProductsMap.get(id);
            if (availableProduct == null) {
                throw new NoSpecifiedProductInWarehouseException("No such product in warehouse" +
                        product.getKey().toString()
                );
            }
            if (availableProduct.getQuantity() >= product.getValue()) {
                Double volume = bookedProductsDto.getDeliveryVolume() +
                        (availableProduct.getWidth() * availableProduct.getHeight() * availableProduct.getDepth())
                                * product.getValue();
                bookedProductsDto.setDeliveryVolume(volume);
                Double weight = bookedProductsDto.getDeliveryWeight() + (availableProduct.getWeight()) * product.getValue();
                bookedProductsDto.setDeliveryWeight(weight);
                if (availableProduct.getFragile()) {
                    bookedProductsDto.setFragile(true);
                }
            } else {
                String message = "Product quantity " + availableProduct.getProductId() + " not enough on ware house. " +
                        "Available count is " + availableProduct.getQuantity();
                throw new ProductInShoppingCartLowQuantityInWarehouse(message);
            }
        }
        return bookedProductsDto;
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Такого товара нет в перечне товаров на складе:" + request.getProductId()));
        Integer oldQuantity = product.getQuantity();
        Integer newQuantity = oldQuantity + request.getQuantity();
        product.setQuantity(newQuantity);
        repository.save(product);

        ProductDto productDto = shoppingStoreClient.getProduct(product.getProductId());
        QuantityState quantityState;
        if (newQuantity > 100) {
            quantityState = QuantityState.MANY;
        } else if (newQuantity > 10) {
            quantityState = QuantityState.ENOUGH;
        } else if (newQuantity > 0) {
            quantityState = QuantityState.FEW;
        } else {
            quantityState = QuantityState.ENDED;
        }
        SetProductQuantityStateRequest stateRequest = new SetProductQuantityStateRequest(product.getProductId(), quantityState);
        shoppingStoreClient.setProductQuantityState(stateRequest);

    }

    @Override
    public AddressDto getWarehouseAddress() {
        final String[] addresses = new String[]{"ADDRESS_1", "ADDRESS_2"};
        final String address = addresses[Random.from(new SecureRandom()).nextInt(0, 1)];
        return AddressDto.builder()
                .city(address)
                .street(address)
                .house(address)
                .country(address)
                .flat(address)
                .build();
    }
}
