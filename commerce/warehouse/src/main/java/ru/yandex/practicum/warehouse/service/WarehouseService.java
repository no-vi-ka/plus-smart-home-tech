package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.contract.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.warehouse.AddressDto;
import ru.yandex.practicum.commerce.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.commerce.request.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.commerce.request.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.WarehouseApp;
import ru.yandex.practicum.warehouse.dal.WarehouseProduct;
import ru.yandex.practicum.warehouse.dal.WarehouseProductRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseProductRepository warehouseProductRepository;

    @Transactional
    public void setProductToWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseProductRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Product " + request.getProductId() + " already exist in warehouse");
        }

        WarehouseProduct product = new WarehouseProduct(
                request.getProductId(),
                request.getFragile(),
                request.getDimension().getWidth(),
                request.getDimension().getHeight(),
                request.getDimension().getDepth(),
                request.getWeight(),
                0
        );
        warehouseProductRepository.save(product);
    }

    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = warehouseProductRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Product " + request.getProductId() + " not found in warehouse"));
        long newQuantity = product.getQuantity() + request.getQuantity();
        product.setQuantity(newQuantity);
        warehouseProductRepository.save(product);
    }

    public AddressDto getAddress() {
        String CURRENT_ADDRESS = WarehouseApp.getCurrentAddress();
        return new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS
        );
    }

    @Transactional
    public BookedProductsDto checkedBookedProducts(ShoppingCartDto cart) {
        BookedProductsDto bookedProducts = new BookedProductsDto();
        Map<UUID, Long> requestedProducts = cart.getProducts();
        List<WarehouseProduct> productsInWarehouse = warehouseProductRepository.findAllById(requestedProducts.keySet());

        if(requestedProducts.size() != productsInWarehouse.size()) {
            Set<UUID> foundIds = productsInWarehouse.stream()
                    .map(WarehouseProduct::getProductId)
                    .collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(requestedProducts.keySet());
            missingIds.removeAll(foundIds);

            throw new NoSpecifiedProductInWarehouseException(
                    "Products not found in warehouse: " + missingIds
            );
        }

        productsInWarehouse.forEach(product -> {
            long requestedQuantity = requestedProducts.get(product.getProductId());
            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Product " + product.getProductId() + " not enough in warehouse");
            }
            double productDeliveryWeight = product.getWeight() * requestedQuantity;
            double productDeliveryVolume = product.getDepth() * product.getHeight() * product.getWidth() * requestedQuantity;

            bookedProducts.setDeliveryWeight(bookedProducts.getDeliveryWeight() + productDeliveryWeight);
            bookedProducts.setDeliveryVolume(bookedProducts.getDeliveryVolume() + productDeliveryVolume);

            if (product.isFragile()) {
                bookedProducts.setFragile(true);
            }
        });
        return bookedProducts;
    }
}
