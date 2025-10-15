package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

@Entity
@Table(name = "actions")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SecondaryTable(name = "scenario_actions", pkJoinColumns = @PrimaryKeyJoinColumn(name = "action_id"))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    ActionTypeAvro type;

    Integer value;

    @ManyToOne
    @JoinColumn(name = "scenario_id", table = "scenario_actions")
    Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "sensor_id", table = "scenario_actions")
    Sensor sensor;
}
