package ru.yandex.practicum.sensors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@AllArgsConstructor
public class SwitchSensor {
    private String id;
    private boolean state;
}
