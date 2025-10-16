package ru.yandex.practicum.snapshot.storage;

import java.util.Optional;

/**
 * Определяет поведение хранилища для снапшотов
 */
public interface SnapshotStorage<T, V> {
    /**
     * Обновляет состояние снапшота
     * @param event - входное событие, с учётом которого обновляется состояние
     * @return - Optional.of(), если состояние было обновлено / Optional.empty(), если состояние не было обновлено
     */
    Optional<T> updateSnapshotByEvent(V event);
}
