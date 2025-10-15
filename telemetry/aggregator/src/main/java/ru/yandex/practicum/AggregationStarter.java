package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.config.kafka.KafkaConfig;
import ru.yandex.practicum.service.AggregatorService;

import java.time.Duration;
import java.util.List;

@Component
public class AggregationStarter implements CommandLineRunner {

    private final Producer<Void, SpecificRecordBase> producer;
    private final Consumer<Void, SpecificRecordBase> consumer;
    private final AggregatorService aggregatorService;
    private final KafkaConfig kafkaConfig;

    public AggregationStarter(AggregatorService aggregatorService, KafkaConfig kafkaConfig) {
        this.producer = new KafkaProducer<>(kafkaConfig.getProducerProperties());
        this.consumer = new KafkaConsumer<>(kafkaConfig.getConsumerProperties());
        this.aggregatorService = aggregatorService;
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public void run(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(kafkaConfig.getTopics().get("sensors")));
            while (true) {
                ConsumerRecords<Void, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<Void, SpecificRecordBase> record : records) {
                    aggregatorService.aggregationSnapshot(producer, record.value());
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {

        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                consumer.close();
                producer.close(Duration.ofSeconds(5));
            }
        }
    }
}