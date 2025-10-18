package ru.yandex.practicum.sensors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TemperatureSensor {
    private String id;
    private int temperature;
}
