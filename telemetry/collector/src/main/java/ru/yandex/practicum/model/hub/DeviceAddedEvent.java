package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.constant.HubEventType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import static ru.yandex.practicum.constant.HubEventType.DEVICE_ADDED;

@Getter
@Setter
@ToString
public class DeviceAddedEvent extends HubEvent {

    @NotBlank
    private String id;
    @NonNull
    private DeviceTypeAvro deviceType;

    public HubEventType getType() {
        return DEVICE_ADDED;
    }

}
