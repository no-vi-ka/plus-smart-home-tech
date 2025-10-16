package ru.yandex.practicum.snapshot.model;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public class SensorsSnapshotAvroAdapter implements Snapshot<SensorsSnapshotAvro, SensorEventAvro> {
    // Снапшот
    private final SensorsSnapshotAvro snapshot;
    // Состояние снапшота, key - SensorEventAvro.id, value - состояние
    private final Map<String, SensorStateAvro> state;

    public SensorsSnapshotAvroAdapter(SensorsSnapshotAvro snapshot) {
        this.snapshot = snapshot;
        state = snapshot.getSensorsState();
    }

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        // id сенсора - ключ к состоянию
        String id = event.getId();
        SensorStateAvro stateAvro = state.get(id);

        // Проверка на необходимость обновления
        if (isNeedUpdate(stateAvro, event)) {
            SensorStateAvro newState = new SensorStateAvro();
            // Устанока новых данных состояния
            newState.setData(event.getPayload());
            newState.setTimestamp(event.getTimestamp());
            state.put(id, newState);
            // Обновление timestamp снапшота
            updateSnapshotTimestamp(event.getTimestamp());
            return Optional.of(snapshot);
        }

        // Состояние и снапшот не нуждаются в изменениях
        return Optional.empty();
    }

    /**
     * Обновление требуется, если [состояние отсутствует в мапе] или [данные в событии отличаются от данных в состоянии
     * и timestamp события позже чем timestamp состояния]
     *
     * @param stateAvro - событие сенсора, которое сравнивается с состоянием в снапшоте
     * @param event     - состояние сенсора в снапшоте
     * @return - true, если необходимо обновление / false, если обновление не требуется
     */
    private boolean isNeedUpdate(SensorStateAvro stateAvro, SensorEventAvro event) {
        // Требуется обновление, если состояние отсутствует в мапе
        boolean notExistInMap = stateAvro == null;

        if (notExistInMap) {
            return true;
        }

        // Не требуется обновление, если event.timestamp < state.timestamp
        boolean oldTimestamp = event.getTimestamp().isBefore(stateAvro.getTimestamp());

        if (oldTimestamp) {
            return false;
        }

        // Код после if (oldTimestamp) возможен только если event.timestamp >= state.timestamp
        // Требуется обновление, если данные отличаются с учётом timestamp
        boolean dataEquals = event.getPayload().equals(stateAvro.getData());

        return !dataEquals;
    }

    private void updateSnapshotTimestamp(Instant timestamp) {
        snapshot.setTimestamp(timestamp);
    }
}
