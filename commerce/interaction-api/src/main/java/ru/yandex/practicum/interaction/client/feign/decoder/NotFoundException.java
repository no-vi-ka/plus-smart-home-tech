package ru.yandex.practicum.interaction.client.feign.decoder;

public class NotFoundException extends Exception {
    public NotFoundException(String msg) {
        super(msg);
    }
}
