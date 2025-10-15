package ru.yandex.practicum.model.sensor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.constant.SensorEventType;

import static ru.yandex.practicum.constant.SensorEventType.MOTION_SENSOR_EVENT;

@Getter
@Setter
@ToString
public class MotionSensorEvent extends SensorEvent {

    private int linkQuality;
    private boolean motion;
    private int voltage;

    @Override
    public SensorEventType getType() {
        return MOTION_SENSOR_EVENT;
    }
}
