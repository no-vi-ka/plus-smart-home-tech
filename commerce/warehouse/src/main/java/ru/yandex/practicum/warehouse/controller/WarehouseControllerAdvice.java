package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.exception.ApiErrorResponse;
import ru.yandex.practicum.interaction.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.interaction.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.interaction.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;

@Slf4j
@RestControllerAdvice
public class WarehouseControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException cause) {
        log.warn("Нарушение валидации аргумента метода", cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiErrorResponse handleConstraintViolationException(ConstraintViolationException cause) {
        log.warn("Нарушение ограничений (constraint)", cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ApiErrorResponse handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException cause) {
        log.warn(cause.getUserMessage(), cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouseException.class)
    public ApiErrorResponse handleProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouseException cause) {
        log.warn(cause.getUserMessage(), cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ApiErrorResponse handleSpecifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException cause) {
        log.warn(cause.getUserMessage(), cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ApiErrorResponse handleThrowable(Throwable cause) {
        log.error("Ошибка", cause);
        return new ApiErrorResponse(cause);
    }
}
