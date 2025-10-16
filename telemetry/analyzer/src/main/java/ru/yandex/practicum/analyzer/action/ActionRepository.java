package ru.yandex.practicum.analyzer.action;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.action.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}