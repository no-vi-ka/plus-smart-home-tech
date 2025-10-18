package ru.yandex.practicum.aggregator.handlers;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class SensorEventHandler {

    Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro eventAvro) {
        String hubId = eventAvro.getHubId();

        if (snapshots.containsKey(hubId)) {
            SensorsSnapshotAvro oldSnapshot = snapshots.get(hubId);
            Optional<SensorsSnapshotAvro> updatedSnapshot = updateSnapshot(oldSnapshot, eventAvro);
            updatedSnapshot.ifPresent(sensorsSnapshotAvro -> snapshots.put(hubId, sensorsSnapshotAvro));
            return updatedSnapshot;
        } else {
            SensorsSnapshotAvro snapshotAvro = createSnapshot(eventAvro);
            snapshots.put(hubId, snapshotAvro);

            return Optional.of(snapshotAvro);
        }
    }

    private Optional<SensorsSnapshotAvro> updateSnapshot(SensorsSnapshotAvro snapshot, SensorEventAvro event) {
        String sensorId = event.getId();

        if (snapshot.getSensorsState().containsKey(sensorId)) {
            if (snapshot.getSensorsState().get(sensorId).getTimestamp().isAfter(event.getTimestamp()) ||
                    snapshot.getSensorsState().get(sensorId).getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro sensorState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(sensorId, sensorState);
        snapshot.setTimestamp(event.getTimestamp());

        return Optional.of(snapshot);
    }

    private SensorsSnapshotAvro createSnapshot(SensorEventAvro event) {
        Map<String, SensorStateAvro> states = new HashMap<>();
        SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        states.put(event.getId(), sensorStateAvro);

        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.now())
                .setSensorsState(states)
                .build();
    }
}
