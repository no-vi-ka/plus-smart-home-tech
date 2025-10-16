package ru.yandex.practicum.shopping.cart.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.client.feign.warehouse.WarehouseFallbackException;
import ru.yandex.practicum.interaction.exception.ApiErrorResponse;
import ru.yandex.practicum.interaction.exception.shopping.cart.NoProductsInShoppingCartException;
import ru.yandex.practicum.interaction.exception.shopping.cart.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.exception.shopping.cart.ShoppingCartDeactivateException;

@Slf4j
@RestControllerAdvice
public class ShoppingCartControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException cause) {
        log.warn("Нарушение валидации аргумента метода", cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiErrorResponse handleConstraintViolations(ConstraintViolationException cause) {
        log.warn("Нарушение ограничений (constraint)", cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthorizedUserException.class)
    public ApiErrorResponse handleNoAuth(NotAuthorizedUserException cause) {
        log.warn("Пользователь не авторизован", cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ApiErrorResponse handleNoProductsInShoppingCart(NoProductsInShoppingCartException cause) {
        log.warn("Нет искомых товаров в корзине", cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ShoppingCartDeactivateException.class)
    public ApiErrorResponse handleShoppingCartDeactivateException(ShoppingCartDeactivateException cause) {
        log.warn("Корзина деактивирована", cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(WarehouseFallbackException.class)
    public ApiErrorResponse handleWarehouseFallbackException(WarehouseFallbackException cause) {
        log.error(cause.getMessage(), cause);
        return new ApiErrorResponse(cause);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ApiErrorResponse handleThrowable(Throwable cause) {
        log.error("Ошибка", cause);
        return new ApiErrorResponse(cause);
    }
}
