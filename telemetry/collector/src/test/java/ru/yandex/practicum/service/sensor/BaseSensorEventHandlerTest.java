package ru.yandex.practicum.service.sensor;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.yandex.practicum.config.KafkaProducerConfig;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.SensorEventHandler;
import ru.yandex.practicum.service.kafka.KafkaEventProducer;
import ru.yandex.practicum.service.kafka.ProducerSendParam;
import ru.yandex.practicum.util.SensorEventHandleFactory;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

/*
Класс для тестирования выполнения handle, SensorEventProto -> ProducerSendParam, проверяется маппинг
 */
@SpringBootTest
@RequiredArgsConstructor
public abstract class BaseSensorEventHandlerTest {
    protected final String id = "senId";
    protected final String hubId = "hubId";
    protected final Timestamp timestamp = Timestamp.newBuilder()
            .setSeconds(999)
            .setNanos(676)
            .build();

    protected final SensorEventHandleFactory sensorEventHandleFactory;
    protected final KafkaProducerConfig kafkaProducerConfig;
    protected final SensorEventAvroMapper sensorEventAvroMapper;
    protected final SensorEventProtoMapper sensorEventProtoMapper;
    protected final SensorEventHandler handler;
    @MockBean
    private KafkaEventProducer kafkaEventProducer;

    // Заполняется из KafkaEventProducerImplTest при вызове send()
    @Captor
    protected ArgumentCaptor<ProducerSendParam> targetParam;

    protected SensorEventAvro targetBase;

    // Создание специфичного proto-объекта для теста
    protected abstract SensorEventProto createSpecificSensorProto();

    // Проверка соответствия специфичных полей входного Proto и выходного Avro
    protected abstract void checkSpecificAvroFields();

    @Test
    void handleProtoToAvro() {
        // Создание прото Event
        SensorEventProto eventProto = createSpecificSensorProto();

        // Обработка proto
        handler.handle(eventProto);

        // Верефикация, что метод был вызван 1 раз
        verify(kafkaEventProducer).send(targetParam.capture());

        // Проверка полей параметра
        ProducerSendParam param = targetParam.getValue();
        checkSuccessProducerSendParamBase(param);

        // Проверка полей SensorEventAvro
        targetBase = (SensorEventAvro) param.getValue();
        checkFieldsSensorEventAvro(targetBase);

        // Проверка соответствия специфичных полей входного Proto и выходного Avro
        checkSpecificAvroFields();
    }

    protected SensorEventProto.Builder fillSensorProtoBaseFields(SensorEventProto.Builder builder) {
        return builder.setId(id)
                .setHubId(hubId)
                .setTimestamp(timestamp);
    }

    private void checkSuccessProducerSendParamBase(ProducerSendParam param) {
        assertEquals(hubId, param.getKey());
        assertEquals(kafkaProducerConfig.getSensorsTopic(), param.getTopic());
        /*
         Проверка временной метки, поскольку nano (в proto) < milli (в param), nano потеряется
         + nano теряется при setTimestamp() сгенерированных классов avro:
         public void setTimestamp(java.time.Instant value) {
           this.timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS); <-- только MILLIS
         }
        */
        Instant expectedInstant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        Instant actualInstant = Instant.ofEpochMilli(param.getTimestamp());
        assertEquals(expectedInstant.toEpochMilli(), actualInstant.toEpochMilli());
    }

    private void checkFieldsSensorEventAvro(SensorEventAvro avro) {
        assertEquals(hubId, avro.getHubId());
        assertEquals(id, avro.getId());
        /*
         nano теряется при setTimestamp() сгенерированных классов avro:
         public void setTimestamp(java.time.Instant value) {
           this.timestamp = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS); <-- только MILLIS
         }
         */
        Instant expectedInstant = Instant.ofEpochSecond(timestamp.getSeconds());
        Instant actualInstant = avro.getTimestamp();
        assertEquals(expectedInstant, actualInstant);
    }
}