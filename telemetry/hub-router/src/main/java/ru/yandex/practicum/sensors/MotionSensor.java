package ru.yandex.practicum.sensors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MotionSensor {
    private String id;
    private int linkQuality;
    private int voltage;
    private boolean motion;
}
