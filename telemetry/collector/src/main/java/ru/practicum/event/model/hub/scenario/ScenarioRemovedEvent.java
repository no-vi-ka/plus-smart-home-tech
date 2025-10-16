package ru.practicum.event.model.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    @NotBlank(message = "Название удаленного сценария не может быть пустым")
    @Size(min = 3, message = "Должно содержать не менее 3 символов")
    private String name;
    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
