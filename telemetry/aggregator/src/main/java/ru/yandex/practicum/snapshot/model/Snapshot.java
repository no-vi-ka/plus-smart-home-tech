package ru.yandex.practicum.snapshot.model;

import java.util.Optional;

public interface Snapshot<T, V> {
    /**
     * Обновляет состояние снапшота
     * @param event - входное событие, с учётом которого обновляется состояние
     * @return - Optional.of(), если состояние было обновлено / Optional.empty(), если состояние не было обновлено
     */
    Optional<T> updateState(V event);
}
