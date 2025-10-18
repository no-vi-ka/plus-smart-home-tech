package ru.yandex.practicum.sensors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LightSensor {
    private String id;
    private int luminosity;
    private int linkQuality;
}
