package ru.yandex.practicum.interaction.client.feign.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorDecoder implements ErrorDecoder {
    // используем стандартный декодер для всех кодов, которые не обработаем явно
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            // обработка ошибки 404 (Not Found)
            case 404 -> {
                return new NotFoundException("Resource not found for method: " + methodKey);
            }
            // обработка ошибки 500 (Internal Server Error)
            case 500 -> {
                return new InternalServerErrorException("Server error occurred");
            }
            // для других кодов используем стандартное поведение
            default -> {
                return defaultDecoder.decode(methodKey, response);
            }
        }
    }
}
