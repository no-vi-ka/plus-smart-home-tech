package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.constant.HubEventType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

import static ru.yandex.practicum.constant.HubEventType.SCENARIO_ADDED;

@Getter
@Setter
@ToString
public class ScenarioAddedEvent extends ScenarioEvent {

    @NonNull
    @Size(min = 1)
    private List<DeviceActionAvro> actions;
    @NonNull
    @Size(min = 1)
    private List<ScenarioConditionAvro> conditions;

    public HubEventType getType() {
        return SCENARIO_ADDED;
    }
}
