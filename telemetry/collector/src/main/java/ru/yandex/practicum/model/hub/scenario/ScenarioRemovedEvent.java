package ru.yandex.practicum.model.hub.scenario;

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
public class ScenarioRemovedEvent extends BaseHubEvent {
    @NotBlank
    String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
