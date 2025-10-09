package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceAction {
	@NotNull
	private String sensorId;
	@NotNull
	private ActionType type;
	Integer value;
}
