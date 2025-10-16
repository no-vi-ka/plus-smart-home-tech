package ru.yandex.practicum.shopping.store.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductDto;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductPageDto;
import ru.yandex.practicum.interaction.dto.shopping.store.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ShoppingStoreService {
    // Получение списка товаров по типу в пагинированном виде
    ProductPageDto getProductsByCategory(ProductCategory category, Pageable pageable);

    // Создание нового товара в ассортименте
    ProductDto createProduct(ProductDto productDto);

    // Обновление товара в ассортименте, например уточнение описания, характеристик и т.д.
    ProductDto updateProduct(ProductDto productDto);

    // Удалить товар из ассортимента магазина. Функция для менеджерского состава.
    Boolean deleteProduct(UUID productId);

    // Установка статуса по товару. API вызывается со стороны склада.
    Boolean setProductQuantityState(SetProductQuantityStateRequest request);

    // Получить сведения по товару из БД.
    ProductDto getProduct(UUID productId);
}
