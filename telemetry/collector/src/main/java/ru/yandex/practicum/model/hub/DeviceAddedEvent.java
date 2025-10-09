package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@Data
public class DeviceAddedEvent extends HubEvent {
	@NotNull
	private String id;
	@NotNull
	private DeviceType deviceType;

	@Override
	public HubEventType getType() {
		return HubEventType.DEVICE_ADDED;
	}
}
