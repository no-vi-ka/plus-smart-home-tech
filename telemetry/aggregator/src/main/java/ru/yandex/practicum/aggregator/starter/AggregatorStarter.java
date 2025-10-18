package ru.yandex.practicum.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.handlers.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorStarter {

    private final KafkaConsumer<String, SpecificRecordBase> kafkaConsumer;
    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;
    private final SensorEventHandler sensorEventHandler;

    @Value("${topics.snapshots}")
    private String snapshotTopic;

    @Value("${topics.sensors}")
    private String sensorTopic;

    public void start() {
        try {
            log.info("Sensor topic = {}", sensorTopic);
            log.info("Snapshot topic = {}", snapshotTopic);
            kafkaConsumer.subscribe(List.of(sensorTopic));

            Runtime.getRuntime().addShutdownHook(new Thread(kafkaConsumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = kafkaConsumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro eventAvro = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> snapshot = sensorEventHandler.updateState(eventAvro);
                    if (snapshot.isPresent()) {
                        ProducerRecord<String, SpecificRecordBase> message = new ProducerRecord<>(snapshotTopic,
                                null, eventAvro.getTimestamp().toEpochMilli(), eventAvro.getHubId(),
                                snapshot.get()
                        );
                        kafkaProducer.send(message);
                    }
                }
                kafkaConsumer.commitSync();
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедится,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы


                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                // здесь нужно вызвать метод консьюмера для фиксиции смещений
                kafkaProducer.flush();
                kafkaConsumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                kafkaConsumer.close();
                log.info("Закрываем продюсер");
                kafkaProducer.close();
            }
        }
    }
}