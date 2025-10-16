package ru.practicum.event.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.SensorEvent;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    @NotNull(message = "Уровень температуры по шкале Цельсия не омжет быть пустым")
    private Integer temperatureC;
    @NotNull(message = "Влажность не может быть пустая")
    private Integer humidity;
    @NotNull(message = "Уровень CO2 не может быть пустым")
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
