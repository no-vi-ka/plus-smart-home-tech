package ru.yandex.practicum.interaction.api.shopping.store;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductCategory;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductDto;
import ru.yandex.practicum.interaction.dto.shopping.store.ProductPageDto;
import ru.yandex.practicum.interaction.dto.shopping.store.QuantityState;

import java.util.UUID;

public interface ShoppingStoreApi {
    // Получение списка товаров по типу в пагинированном виде
    @GetMapping
    ProductPageDto getProductsByCategory(@RequestParam ProductCategory category, Pageable pageable);

    // Создание нового товара в ассортименте
    @PutMapping
    ProductDto createProduct(@Valid @RequestBody ProductDto productDto);


    // Обновление товара в ассортименте, например уточнение описания, характеристик и т.д.
    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    // Удалить товар из ассортимента магазина. Функция для менеджерского состава.
    @PostMapping("/removeProductFromStore")
    public Boolean deleteProduct(@RequestBody UUID productId);

    // Установка статуса по товару. API вызывается со стороны склада.
    @PostMapping("/quantityState")
    Boolean setProductQuantityState(@RequestParam UUID productId,
                                    @RequestParam QuantityState quantityState);

    // Получить сведения по товару из БД.
    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
