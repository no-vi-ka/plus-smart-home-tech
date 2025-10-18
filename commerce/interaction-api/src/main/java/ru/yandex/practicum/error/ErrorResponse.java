package ru.yandex.practicum.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ErrorResponse {
    private Throwable cause;
    private List<StackTraceElement> stackTrace;
    private String httpStatus;
    private String userMessage;
    private String message;
    private List<Throwable> suppressed;
    private String localizedMessage;
}
