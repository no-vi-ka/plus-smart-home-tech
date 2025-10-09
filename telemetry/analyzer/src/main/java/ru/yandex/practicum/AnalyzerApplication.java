package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerApplication {

    public static void main(String[] args) {
        try (ConfigurableApplicationContext context =
                     SpringApplication.run(AnalyzerApplication.class, args)) {

            HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);
            SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);

            Thread hubEventsThread = new Thread(hubEventProcessor, "HubEventHandlerThread");
            hubEventsThread.start();

            snapshotProcessor.run();

        } catch (Exception e) {
            System.err.println("An error occurred while running the application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}