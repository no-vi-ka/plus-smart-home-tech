package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScenarioCondition {
	@NotBlank
	private String sensorId;
	@NotNull
	private ScenarioConditionType type;
	@NotNull
	private ScenarioConditionOperation operation;
	@NotNull
	Object value;
}
