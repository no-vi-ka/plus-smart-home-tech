package ru.yandex.practicum.analyzer.service.snapshot;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.condition.model.Condition;
import ru.yandex.practicum.analyzer.condition.model.ConditionOperation;
import ru.yandex.practicum.analyzer.condition.model.ConditionType;
import ru.yandex.practicum.analyzer.scenario.ScenarioRepository;
import ru.yandex.practicum.analyzer.scenario.model.Scenario;
import ru.yandex.practicum.analyzer.scenario.model.ScenarioAction;
import ru.yandex.practicum.analyzer.scenario.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.service.snapshot.condition.ConditionValueHandler;
import ru.yandex.practicum.analyzer.util.ConditionValueHandlerFactory;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SnapshotServiceImpl implements SnapshotService {

    private final HubRouterControllerBlockingStub hubRouterClient;
    private final ScenarioRepository scenarioRepository;
    private final ConditionValueHandlerFactory conditionValueHandlerFactory;

    public SnapshotServiceImpl(@GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouterClient, ScenarioRepository scenarioRepository, ConditionValueHandlerFactory conditionValueHandlerFactory) {
        this.hubRouterClient = hubRouterClient;
        this.scenarioRepository = scenarioRepository;
        this.conditionValueHandlerFactory = conditionValueHandlerFactory;
    }

    @Transactional(readOnly = true)
    public void handleSnapshot(SensorsSnapshotAvro snapshotAvro) {
        String hubId = snapshotAvro.getHubId();
        log.info("start handle snapshot hubId={}", hubId);

        // Сценарии, связанные с хабом
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        // мапа key - id сенсора, value - состояние
        Map<String, SensorStateAvro> sensorStateMap = snapshotAvro.getSensorsState();

        // обход сценариев с проверкой условий
        for (Scenario scenario : scenarios) {
            log.trace("start handle hubId={}, scenarioId={}", scenario.getHubId(), scenario.getId());
            boolean allConditionsMet = checkScenarioConditions(scenario.getScenarioConditions(), sensorStateMap);
            log.debug("allConditionsMet={}", allConditionsMet);
            if (allConditionsMet) {
                for (ScenarioAction action : scenario.getScenarioActions()) {
                    DeviceActionRequest request = buildRequest(scenario, action, snapshotAvro.getTimestamp());
                    log.debug("actionId={}, req fields={}", action.getId(), request.getAllFields());
                    try {
                        hubRouterClient.handleDeviceAction(request);
                    } catch (Exception e) {
                        log.error("Ошибка при обработке hubRouterClient", e);
                    }
                }
            }
        }
    }

    /**
     * Проверяет, выполнены ли все условия сценария
     */
    private boolean checkScenarioConditions(Set<ScenarioCondition> scenarioConditions,
                                            Map<String, SensorStateAvro> sensorStateMap) {
        for (ScenarioCondition scenarioCondition : scenarioConditions) {

            String sensorId = scenarioCondition.getSensor().getId();

            Condition condition = scenarioCondition.getCondition(); // условие (из БД)
            SensorStateAvro sensorState = sensorStateMap.get(sensorId); // состояние (из снапшота)

            // если не выполняется хотя бы одно условие, произойдёт выход из цикла
            if (!isConditionMet(condition, sensorState)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет выполнение отдельного условия
     *
     * @param condition   - условие (из БД)
     * @param sensorState - состояние (из снапшота)
     * @return true, если условие выполняется, иначе false
     */
    private boolean isConditionMet(Condition condition, SensorStateAvro sensorState) {
        // Если состояние не найдено в снапшоте, условие не выполняется

        boolean hasState = sensorState != null;
        log.debug("hasState={}", hasState);

        if (!hasState) {
            return false;
        }

        Integer compareValue = condition.getValue();
        log.debug("compareValue={}", compareValue);

        // Значение может быть null
        if (compareValue == null) {
            log.debug("compareValue=null");
            return false;
        }

        // Тип условия
        ConditionType type = condition.getType();

        // Получаем обработчика значения
        ConditionValueHandler handler = conditionValueHandlerFactory.getHandlerByConditionType(type);

        // Получаем конкретное значение из состояния снапшота
        Integer sensorValue = handler.handleValue(sensorState.getData());
        log.debug("sensorValue={}", sensorValue);

        // Оператор сравнения и сравниваемое значение
        ConditionOperation operation = condition.getOperation();

        return switch (operation) {
            case EQUALS -> sensorValue.compareTo(compareValue) == 0;
            case GREATER_THAN -> sensorValue.compareTo(compareValue) > 0;
            case LOWER_THAN -> sensorValue.compareTo(compareValue) < 0;
        };
    }

    /**
     * Создаёт DeviceActionRequest, .setValue() обрабатывает возможный null в action.getAction().getValue()
     */
    private DeviceActionRequest buildRequest(Scenario scenario, ScenarioAction action, Instant timestamp) {

        DeviceActionProto.Builder builderAction = DeviceActionProto.newBuilder();

        DeviceActionRequest.Builder builder = DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(builderAction
                        .setSensorId(action.getId().getSensorId())
                        .setType(ActionTypeProto.valueOf(action.getAction().getType().name())))
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano()));

        boolean actionHasValue = action.getAction().getValue() != null;
        log.debug("actionHasValue={}", actionHasValue);
        if (actionHasValue) {
            builderAction.setValue(action.getAction().getValue());
        }

        return builder.build();
    }
}
