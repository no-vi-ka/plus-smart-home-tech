package ru.yandex.practicum.shopping.store.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.exception.ApiErrorResponse;
import ru.yandex.practicum.interaction.exception.shopping.store.ProductNotFoundException;

@RestControllerAdvice
@Slf4j
public class ShoppingStoreControllerAdvice {
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleProductNotFoundException(ProductNotFoundException e) {
        log.warn(e.getMessage(), e);
        return new ApiErrorResponse(e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.warn(e.getMessage(), e);
        return new ApiErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleThrowable(Throwable e) {
        log.error(e.getMessage(), e);
        return new ApiErrorResponse(e);
    }
}
