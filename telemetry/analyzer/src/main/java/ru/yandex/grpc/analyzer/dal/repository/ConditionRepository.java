package ru.yandex.grpc.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.grpc.analyzer.dal.entity.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
