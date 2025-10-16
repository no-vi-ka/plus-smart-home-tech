package ru.yandex.grpc.aggregator.service;

import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SnapshotServiceImpl implements SnapshotService {
    private final Map<String, SensorsSnapshotAvro> snapshotsInMemory = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        SensorsSnapshotAvro existingSnapshot = snapshotsInMemory.get(hubId);
        Map<String, SensorStateAvro> newStateMap;
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        if (existingSnapshot == null) {
            newStateMap = new HashMap<>();
        } else {
            if (isOutdatedOrDuplicate(existingSnapshot, event, newState)) {
                return Optional.empty();
            }
            newStateMap = new HashMap<>(existingSnapshot.getSensorsState());
        }

        newStateMap.put(event.getId(), newState);
        SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                .setSensorsState(newStateMap)
                .setTimestamp(event.getTimestamp())
                .setHubId(hubId)
                .build();

        snapshotsInMemory.put(event.getHubId(), snapshot);
        return Optional.of(snapshot);
    }

    private boolean isOutdatedOrDuplicate(SensorsSnapshotAvro existingSnapshot, SensorEventAvro event, SensorStateAvro newState) {
        SensorStateAvro existingState = existingSnapshot.getSensorsState().get(event.getId());
        if (existingState == null) {
            return false;
        }

        if (existingSnapshot.getTimestamp().isAfter(event.getTimestamp())) {
            return true;
        }

        return existingSnapshot.getTimestamp().equals(event.getTimestamp()) &&
                GenericData.get().compare(
                        existingState.getData(),
                        newState.getData(),
                        ((SpecificRecordBase) existingState.getData()).getSchema()) == 0;

    }
}
