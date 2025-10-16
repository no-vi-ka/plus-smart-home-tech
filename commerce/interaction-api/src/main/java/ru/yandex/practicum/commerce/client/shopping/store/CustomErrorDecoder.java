package ru.yandex.practicum.commerce.client.shopping.store;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import ru.yandex.practicum.commerce.contract.shopping.store.exception.ProductNotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            String message = extractMessage(response);
            return new ProductNotFoundException(message);
        }
        return defaultDecoder.decode(methodKey, response);
    }

    private String extractMessage(Response response) {
        if (response.body() == null) {
            return "No error message available";
        }

        try {
            return IOUtils.toString(response.body().asReader(StandardCharsets.UTF_8));
        } catch (IOException e) {
            return "Failed to extract error message from exception response";
        }
    }
}
