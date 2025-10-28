package ru.yandex.practicum.warehouse.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.api.exception.AppError;
import ru.yandex.practicum.interaction.api.exception.AppErrorMultiple;
import ru.yandex.practicum.interaction.api.exception.warehouse.MultipleProductsNotFoundException;
import ru.yandex.practicum.interaction.api.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.api.exception.warehouse.OrderBookingNotFoundException;
import ru.yandex.practicum.interaction.api.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.api.exception.warehouse.ProductInShoppingCartNotInWarehouse;
import ru.yandex.practicum.interaction.api.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;

@Slf4j
@RestControllerAdvice
public class AppErrorWarehouseHandler {

    @ExceptionHandler(ProductInShoppingCartNotInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleProductInShoppingCartNotInWarehouse(ProductInShoppingCartNotInWarehouse exp) {
        log.warn(exp.getMessage(), exp);
        return new AppError("ОШИБКА: ТОВАР ИЗ КОРЗИНЫ ОТСУСТВУЕТ НА СКЛАДЕ " + exp.getMessage());
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException exp) {
        log.warn(exp.getMessage(), exp);
        return new AppError("ОШИБКА: НЕТ УКАЗАННЫХ ТОВАРОВ НА СКЛАДЕ " + exp.getMessage());
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleProductInShoppingCartLowQuantityInWarehouseException(ProductInShoppingCartLowQuantityInWarehouse exp) {
        log.warn(exp.getMessage(), exp);
        return new AppError("ОШИБКА: НА СКЛАДЕ МЕНЬШЕ ТОВАРА ЧЕМ В КОРЗИНЕ " + exp.getMessage());
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleSpecifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException exp) {
        log.warn(exp.getMessage(), exp);
        return new AppError("ОШИБКА: УКАЗАННЫЙ ТОВАР УЖЕ ЕСТЬ НА СКЛАДЕ " + exp.getMessage());
    }

    @ExceptionHandler(OrderBookingNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleOrderBookingNotFoundException(OrderBookingNotFoundException exp) {
        log.warn(exp.getMessage(), exp);
        return new AppError("ОШИБКА: ДЛЯ УКАЗАННОГО ЗАКАЗА НЕТ БРОНИРОВАНИЯ НА СКЛАДЕ " + exp.getMessage());
    }

    @ExceptionHandler(MultipleProductsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppErrorMultiple handleMultipleProductsNotFoundException(MultipleProductsNotFoundException exp) {
        log.error("Ошибка при обновлении товаров: {}", exp.getMessage());
        return new AppErrorMultiple(
                "PRODUCTS_NOT_FOUND",
                exp.getMessage(),
                exp.getMissingProductIds()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleValidationException(MethodArgumentNotValidException exp) {
        log.error("ОШИБКА VALIDATION-WAREHOUSE", exp);
        return new AppError("VALIDATION EXCEPTION" + exp.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppError handleConstraintViolations(ConstraintViolationException exp) {
        log.error("ОШИБКА CONSTRAINT-WAREHOUSE", exp);
        return new AppError("ОШИБКА ОГРАНИЧЕНИЙ CONSTRAINT " + exp.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppError handleThrowable(Throwable exp) {
        log.error("ОШИБКА-WAREHOUSE", exp);
        return new AppError("INTERNAL SERVER ERROR " + exp.getMessage());
    }
}