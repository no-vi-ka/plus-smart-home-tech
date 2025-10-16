package ru.yandex.practicum.analyzer.scenario.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.analyzer.action.model.Action;
import ru.yandex.practicum.analyzer.sensor.model.Sensor;

// Lombok annotations
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// JPA annotations
@Entity
@Table(name = "scenario_actions")
public class ScenarioAction {

    @EmbeddedId
    ScenarioActionId id;

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    Sensor sensor;

    @ManyToOne
    @MapsId("actionId")
    @JoinColumn(name = "action_id")
    Action action;

    /**
     * Конструктор инициализирует составной ключ.
     */
    public ScenarioAction(Scenario scenario, Sensor sensor, Action action) {
        this.scenario = scenario;
        this.sensor = sensor;
        this.action = action;
        // Инициализация составного ключа
        this.id = new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId());
    }
}
