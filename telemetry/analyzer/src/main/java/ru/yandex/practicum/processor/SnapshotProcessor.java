package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    private final AnalyzerProcessor analyzerProcessor;

    @Override
    public void run() {
        log.info("SnapshotProcessor started");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.info("SnapshotProcessor interrupted, shutting down");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @KafkaListener(topics = "telemetry.snapshots.v1", groupId = "telemetry-snapshots")
    public void onMessage(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        SensorsSnapshotAvro snapshot = record.value();
        if (snapshot == null) {
            log.warn("Received null snapshot from Kafka");
            return;
        }

        try {
            analyzerProcessor.processSnapshot(snapshot);
            log.info("Processed snapshot for hub: {}", snapshot.getHubId());
        } catch (Exception e) {
            log.error("Error processing snapshot for hub {}: {}", snapshot.getHubId(), e.getMessage(), e);
        }
    }
}