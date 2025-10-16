package ru.yandex.practicum.model.hub.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAction {
    @NotBlank
    String sensorId;

    @NotNull
    DeviceActionType type;

    Integer value;
}
