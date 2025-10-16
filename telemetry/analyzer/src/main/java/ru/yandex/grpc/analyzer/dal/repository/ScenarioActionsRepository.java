package ru.yandex.grpc.analyzer.dal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.grpc.analyzer.dal.entity.ScenarioAction;
import ru.yandex.grpc.analyzer.dal.entity.ScenarioActionId;

public interface ScenarioActionsRepository extends JpaRepository<ScenarioAction, ScenarioActionId> {
    @Modifying
    @Query("DELETE FROM ScenarioAction sa WHERE sa.id.sensorId = :sensorId")
    void deleteBySensorId(@Param("sensorId") String sensorId);
}
