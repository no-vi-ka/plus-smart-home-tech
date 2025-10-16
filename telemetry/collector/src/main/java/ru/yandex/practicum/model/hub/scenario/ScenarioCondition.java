package ru.yandex.practicum.model.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {
    @NotBlank
    String sensorId;

    @NotNull
    ScenarioConditionType type;

    @NotNull
    ScenarioOperationType operation;

    @NotNull
    Object value;
}
