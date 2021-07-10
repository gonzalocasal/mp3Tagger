package com.tagmp3;

import com.tagmp3.service.FilesProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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
        filesProcessor.process();
    }

}