package ru.yandex.practicum.interaction.client.feign.decoder;

public class InternalServerErrorException extends Exception {
    public InternalServerErrorException(String msg) {
        super(msg);
    }
}
