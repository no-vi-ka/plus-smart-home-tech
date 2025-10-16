package ru.practicum.event.model.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = SensorEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT")
})
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class SensorEvent {
    @NotBlank
    String id;

    @NotBlank
    String hubId;

    Instant timestamp = Instant.now();

    @NotNull
    public abstract SensorEventType getType();
}