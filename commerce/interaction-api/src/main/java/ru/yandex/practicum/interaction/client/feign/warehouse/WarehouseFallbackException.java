package ru.yandex.practicum.interaction.client.feign.warehouse;

public class WarehouseFallbackException extends RuntimeException {
    public WarehouseFallbackException() {
        super("warehouse временно недоступен");
    }
}
