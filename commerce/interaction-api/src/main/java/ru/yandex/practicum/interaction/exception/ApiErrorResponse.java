package ru.yandex.practicum.interaction.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ApiErrorResponse {
    Throwable cause;

    StackTraceElement[] stackTrace;

    String httpStatus;

    String userMessage;

    String message;

    String localizedMessage;

    public ApiErrorResponse(BaseServiceException serviceException) {
        this.cause = serviceException;
        this.stackTrace = serviceException.getStackTrace();
        this.httpStatus = serviceException.getHttpStatus();
        this.userMessage = serviceException.getUserMessage();
        this.message = serviceException.getMessage();
        this.localizedMessage = serviceException.getLocalizedMessage();
    }

    public ApiErrorResponse(Throwable cause) {
        this.cause = cause;
        this.stackTrace = cause.getStackTrace();
        this.httpStatus = "500";
        this.userMessage = "unknown error";
        this.message = cause.getMessage();
        this.localizedMessage = cause.getLocalizedMessage();
    }
}
