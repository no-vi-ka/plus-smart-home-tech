package ru.yandex.practicum.sensors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClimateSensor {
    private String id;
    private int temperature;
    private int humidity;
    private int co2Level;
}
