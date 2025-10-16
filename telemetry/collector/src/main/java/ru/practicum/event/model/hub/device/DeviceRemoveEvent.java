package ru.practicum.event.model.hub.device;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemoveEvent extends HubEvent {
    @NotBlank(message = "Идентификатор удаленного устройства не может быть пустым")
    private String id;
    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
