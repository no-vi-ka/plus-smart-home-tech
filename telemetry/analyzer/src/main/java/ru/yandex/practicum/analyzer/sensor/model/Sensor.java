package ru.yandex.practicum.analyzer.sensor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

// Lombok annotations
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// JPA annotations
@Entity
@Table(name = "sensors")
public class Sensor {
    @Id
    @Column(name = "id")
    String id;

    @Column(name = "hub_id")
    String hubId;
}
