package ru.yandex.practicum.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scenarios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "name"}))
@Getter
@Setter
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "scenario_conditions",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private Set<Condition> conditions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "scenario_actions",
            joinColumns = @JoinColumn(name = "scenario_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> actions = new HashSet<>();
}