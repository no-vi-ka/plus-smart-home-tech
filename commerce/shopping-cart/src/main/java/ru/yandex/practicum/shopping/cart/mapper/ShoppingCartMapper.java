package ru.yandex.practicum.shopping.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.interaction.dto.shopping.cart.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.model.ShoppingCart;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShoppingCartMapper {
    ShoppingCart toShoppingCart(ShoppingCartDto shoppingCartDto);

    ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart);
}
