package ru.practicum.event.model.hub.scenario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.HubEvent;
import ru.practicum.event.model.hub.HubEventType;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @NotBlank(message = "Название добавленного сценария не может быть пустым")
    @Size(min = 3, message = "Должно содержать не менее 3 символов")
    private String name;
    @NotNull(message = "Список условий, которые связаны со сценарием не может быть пустым.")
    private List<Conditions> conditions;
    @NotNull(message = "Список действий, которые должны быть выполнены в рамках сценария. Не может быть пустым.")
    private List<Actions> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
