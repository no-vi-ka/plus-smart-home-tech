package ru.yandex.practicum.warehouse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.interaction.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.interaction.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.AddressMapper;
import ru.yandex.practicum.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.warehouse.model.Address;
import ru.yandex.practicum.warehouse.model.Dimension;
import ru.yandex.practicum.warehouse.model.Product;
import ru.yandex.practicum.interaction.util.ProductNotEnough;
import ru.yandex.practicum.warehouse.repository.AddressRepository;
import ru.yandex.practicum.warehouse.repository.ProductRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Transactional(readOnly = true)
@Slf4j
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;

    // инициализируется тестовыми данными при создании склада
    private final UUID addressId;

    public WarehouseServiceImpl(ProductRepository productRepository,
                                AddressRepository addressRepository,
                                ProductMapper productMapper,
                                AddressMapper addressMapper) {
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.productMapper = productMapper;
        this.addressMapper = addressMapper;

        String[] address = {"ADDRESS_1", "ADDRESS_2"};
        int randomIdx = Random.from(new SecureRandom()).nextInt(0, address.length);
        this.addressId = addressRepository.save(Address.createTestAddress(address[randomIdx])).getId();
    }

    @Transactional
    @Override
    public void newProduct(NewProductInWarehouseRequest newRequest) {
        log.trace("start newProduct newRequest={}", newRequest);

        UUID productId = newRequest.getProductId();

        // проверка, есть ли такой товар уже на складе
        productRepository.findById(productId).ifPresent(product -> {
            log.warn("Товар с id={} уже зарегистрирован", productId);
            throw new SpecifiedProductAlreadyInWarehouseException();
        });

        Product product = productMapper.toProduct(newRequest);
        productRepository.save(product);

        log.trace("start newProduct newRequest={}, productId={}", newRequest, product.getProductId());
    }

    @Override
    public BookedProductsDto checkProducts(ShoppingCartDto shoppingCartDto) {
        log.trace("start checkProducts shoppingCartDto={}", shoppingCartDto);

        // Загружаем из БД товары для проверки
        Set<UUID> ids = shoppingCartDto.getProducts().keySet();
        Map<UUID, Product> productById = productRepository.findAllAsMapByIds(ids);

        // Начинаем проверку.
        BookedProductsDto result = BookedProductsDto.builder() // общая информация о товарах для заказа
                .deliveryVolume(0.0)
                .deliveryWeight(0.0)
                .fragile(false)
                .build();

        List<ProductNotEnough> productsNotEnough = new ArrayList<>(); // товары, которых недостаточно на складе.
        List<UUID> productsNotFound = new ArrayList<>(); // товары, которых нет в БД склада.

        for (Map.Entry<UUID, Integer> entry : shoppingCartDto.getProducts().entrySet()) {
            UUID id = entry.getKey();
            Integer wantedCount = entry.getValue();

            if (!productById.containsKey(id)) {
                productsNotFound.add(id);
                continue;
            }

            // Товар из БД
            Product product = productById.get(id);

            Integer availableCount = product.getQuantity();

            if (wantedCount > availableCount) {
                productsNotEnough.add(new ProductNotEnough(id, availableCount, wantedCount));
                continue;
            }

            // Если товар есть на складе и его достаточное количество, заполняем результат
            Dimension dimension = product.getDimension();

            // Объем
            Double currentVolume = result.getDeliveryVolume();
            Double addVolume = dimension.getHeight() * dimension.getWidth() * dimension.getDepth();
            Double newVolume = currentVolume + addVolume;
            result.setDeliveryVolume(newVolume);

            // Вес
            Double currentWeight = result.getDeliveryWeight();
            Double addWeight = product.getWeight() * wantedCount;
            Double newWeight = currentWeight + addWeight;
            result.setDeliveryWeight(newWeight);

            // Признак хрупкости, если хотя бы 1 товар хрупкий, заказ считается хрупким
            if (product.getFragile() != null) {
                boolean fragile = result.isFragile() || product.getFragile(); // true true -> t, tf->t,  ff->f
                result.setFragile(fragile);
            }
        }

        // если есть товары, информации о которых нет на складе
        if (!productsNotFound.isEmpty()) {
            log.warn("Нет информации о товарах на складе ID: {}", productsNotFound);
            throw new NoSpecifiedProductInWarehouseException(productsNotFound);
        }

        // если есть товары, количество которых на складе недостаточно
        if (!productsNotEnough.isEmpty()) {
            log.warn("Недостаточно товаров на складе: {}", productsNotEnough);
            throw new ProductInShoppingCartLowQuantityInWarehouseException(productsNotEnough);
        }

        log.trace("end checkProducts shoppingCartDto={}, bookedProductsDto={}", shoppingCartDto, result);
        return result;
    }

    @Transactional
    @Override
    public void addProduct(AddProductToWarehouseRequest addRequest) {
        log.trace("start addProduct addRequest={}", addRequest);

        UUID productId = addRequest.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Нет информации о товаре на складе productId={}", productId);
                    return new NoSpecifiedProductInWarehouseException(List.of(productId));
                });

        Integer currentQuantity = product.getQuantity();
        Integer addQuantity = addRequest.getQuantity();
        Integer newQuantity = currentQuantity + addQuantity;

        product.setQuantity(newQuantity);

        log.trace("end addProduct addRequest={}, newQuantity={}", addRequest, newQuantity);
    }

    /**
     * @implNote Сейчас возвращаются временные данные
     */
    @Override
    public AddressDto getAddress() {
        log.trace("start getAddress");
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalStateException("Адрес не найден в БД, id = " + addressId));
        AddressDto addressDto = addressMapper.toAddressDto(address);
        log.trace("end getAddress address={}", addressDto);
        return addressDto;
    }
}
