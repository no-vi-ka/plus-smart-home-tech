package ru.yandex.practicum.shoppingcart.dal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.commerce.dto.shopping.cart.ShoppingCartDto;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShoppingCartMapper {
    @Mapping(source = "id", target = "id")
    ShoppingCartDto toDto(ShoppingCart cart);

    ShoppingCart toEntity(ShoppingCartDto cartDto);
}