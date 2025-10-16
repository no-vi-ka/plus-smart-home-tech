package ru.yandex.practicum.interaction.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Базовый класс-исключение для сервисов
 */

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseServiceException extends RuntimeException {
    String httpStatus;
    String userMessage;
}
