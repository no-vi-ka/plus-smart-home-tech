package ru.yandex.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@RequiredArgsConstructor
@Component
public class ToDto implements Converter<ShoppingCart, ShoppingCartDto> {

    @Override
    public ShoppingCartDto convert(ShoppingCart source) {

        return ShoppingCartDto.builder()
                .shoppingCartId(source.getShoppingCartId())
                .products(source.getProducts())
                .build();
    }
}
