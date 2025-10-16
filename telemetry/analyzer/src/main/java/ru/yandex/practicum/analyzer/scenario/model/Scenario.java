package ru.yandex.practicum.analyzer.scenario.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

// Lombok annotations
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// JPA annotations
@Entity
@Table(name = "scenarios")
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "hub_id")
    String hubId;

    @Column(name = "name")
    String name;

    @Builder.Default
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ScenarioCondition> scenarioConditions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ScenarioAction> scenarioActions = new HashSet<>();

    /**
     * Вспомогательный метод для добавления ScenarioCondition.
     * Устанавливает двустороннюю связь.
     *
     * @param scenarioCondition Сущность ScenarioCondition для добавления.
     */
    public void addScenarioCondition(ScenarioCondition scenarioCondition) {
        this.scenarioConditions.add(scenarioCondition);
        scenarioCondition.setScenario(this);
    }

    /**
     * Вспомогательный метод для удаления ScenarioCondition.
     * Разрывает двустороннюю связь.
     *
     * @param scenarioCondition Сущность ScenarioCondition для удаления.
     */
    public void removeScenarioCondition(ScenarioCondition scenarioCondition) {
        this.scenarioConditions.remove(scenarioCondition);
        scenarioCondition.setScenario(null);
    }

    /**
     * Вспомогательный метод для добавления ScenarioAction.
     * Устанавливает двустороннюю связь.
     *
     * @param scenarioAction Сущность ScenarioAction для добавления.
     */
    public void addScenarioAction(ScenarioAction scenarioAction) {
        this.scenarioActions.add(scenarioAction);
        scenarioAction.setScenario(this);
    }

    /**
     * Вспомогательный метод для удаления ScenarioAction.
     * Разрывает двустороннюю связь.
     *
     * @param scenarioAction Сущность ScenarioAction для удаления.
     */
    public void removeScenarioAction(ScenarioAction scenarioAction) {
        this.scenarioActions.remove(scenarioAction);
        scenarioAction.setScenario(null);
    }
}
