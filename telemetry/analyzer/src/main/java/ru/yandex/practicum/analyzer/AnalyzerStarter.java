package ru.yandex.practicum.analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.hub.HubEventProcessor;
import ru.yandex.practicum.analyzer.service.snapshot.SnapshotProcessor;

/**
 * Запускает выполнение процессов
 */
@Component
@RequiredArgsConstructor
public class AnalyzerStarter implements CommandLineRunner {
    private final HubEventProcessor hubEventProcessor;
    private final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) {
        // Создание потока для обработки hub event
        Thread hubEventThread = new Thread(hubEventProcessor);
        hubEventThread.setName("HubEventThread");
        hubEventThread.start();

        // Создание потока для обработки snapshot
        Thread snapshotThread = new Thread(snapshotProcessor);
        snapshotThread.setName("SnapshotThread");
        snapshotThread.start();
    }
}
