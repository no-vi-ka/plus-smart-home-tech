package ru.practicum.event.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.event.model.hub.device.ConditionType;


@Getter
@Setter
@ToString
public class Conditions {
    private String sensorId;
    private ConditionType type;
    private OperationType operation;
    private Integer value;
}
