package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;

public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    private final String httpStatus = HttpStatus.BAD_REQUEST.toString();
    private final String userMessage;

    public SpecifiedProductAlreadyInWarehouseException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public String getUserMessage() {
        return userMessage;
    }
}

