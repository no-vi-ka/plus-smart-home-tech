package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;

public class CartDeactivatedException extends RuntimeException {
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    private final String userMessage;
    public CartDeactivatedException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public String getHttpStatus() {
        return httpStatus.value() + " " + httpStatus.name();
    }

    public String getUserMessage() {
        return userMessage;
    }
}
