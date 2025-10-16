package ru.yandex.practicum.commerce.client.warehouse;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import ru.yandex.practicum.commerce.contract.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.contract.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.commerce.contract.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = extractMessage(response);
        if (response.status() == 400) {
            if (message.contains("not found")) {
                return new NoSpecifiedProductInWarehouseException(message);
            } else if (message.contains("low quantity")) {
                return new ProductInShoppingCartLowQuantityInWarehouse(message);
            } else if (message.contains("already in warehouse")) {
                return new SpecifiedProductAlreadyInWarehouseException(message);
            }
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
