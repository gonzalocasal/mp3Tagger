package com.tagmp3;

import com.tagmp3.service.FilesProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
@Log4j2
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final FilesProcessor filesProcessor;

    public Application(FilesProcessor filesProcessor) {
        this.filesProcessor = filesProcessor;
    }

    @Override
    public void run(String... args) {
        Instant start = Instant.now();
        filesProcessor.process();
        log.info("Execution Completed in: {} ms.", Duration.between(start, Instant.now()).toMillis());
    }

}