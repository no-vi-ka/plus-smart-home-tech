package ru.practicum.event.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.SensorEvent;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {
    @NotNull(message = "Качество связи не может быть пустым")
    private Integer linkQuality;
    @NotNull(message = "Наличие/отсутствие движения не может быть пустым")
    private Boolean motion;
    @NotNull(message = "Напряжение не может быть пустым")
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
