package ru.yandex.practicum.dto;

public record ErrorResponse(int status, String error, String message, String path) {}