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
import ru.yandex.practicum.analyzer.condition.model.Condition;
import ru.yandex.practicum.analyzer.sensor.model.Sensor;

// Lombok annotations
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// JPA annotations
@Entity
@Table(name = "scenario_conditions")
public class ScenarioCondition {
    @EmbeddedId
    ScenarioConditionId id;

    @ManyToOne
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id")
    Scenario scenario;

    @ManyToOne
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id")
    Sensor sensor;

    @ManyToOne
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id")
    Condition condition;

    /**
     * Конструктор инициализирует составной ключ.
     */
    public ScenarioCondition(Scenario scenario, Sensor sensor, Condition condition) {
        this.scenario = scenario;
        this.sensor = sensor;
        this.condition = condition;
        // Инициализация составного ключа
        this.id = new ScenarioConditionId(scenario.getId(), sensor.getId(), condition.getId());
    }
}
