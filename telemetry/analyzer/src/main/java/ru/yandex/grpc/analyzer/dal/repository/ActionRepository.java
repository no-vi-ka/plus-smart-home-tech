package ru.yandex.grpc.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.grpc.analyzer.dal.entity.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
