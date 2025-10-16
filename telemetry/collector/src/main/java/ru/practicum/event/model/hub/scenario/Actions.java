package ru.practicum.event.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Actions {
    private String sensorId;
    private ActionsType type;
    private Integer value;
}
