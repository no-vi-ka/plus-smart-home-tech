package ru.yandex.practicum.shoppingstore.dal;

import org.mapstruct.Mapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.commerce.dto.shopping.store.PageableDto;

@Mapper(componentModel = "spring", imports = {Sort.class, PageRequest.class})
public interface PageableMapper {
    default Pageable toSpringPageable(PageableDto pageableDto) {
        if (pageableDto.getSort() == null || pageableDto.getSort().length != 2) {
            return PageRequest.of(pageableDto.getPage(), pageableDto.getSize());
        }

        String property = pageableDto.getSort()[0];
        Sort.Direction direction = Sort.Direction.fromString(pageableDto.getSort()[1]);

        return PageRequest.of(
                pageableDto.getPage(),
                pageableDto.getSize(),
                Sort.by(direction, property)
        );
    }
}
