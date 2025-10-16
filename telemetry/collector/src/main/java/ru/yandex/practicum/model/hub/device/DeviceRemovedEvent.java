package ru.yandex.practicum.model.hub.device;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.model.hub.BaseHubEvent;
import ru.yandex.practicum.model.hub.HubEventType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceRemovedEvent extends BaseHubEvent {
    @NotBlank
    String id;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
