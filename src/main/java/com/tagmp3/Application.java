package com.tagmp3;

import com.tagmp3.service.FilesProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;

public class Application {

    static final Logger log = LogManager.getLogger(Application.class.getName());

    public static void main(String[] args) {
        java.util.logging.LogManager.getLogManager().reset();
        log.info("Execution Started");
        Instant start = Instant.now();
        FilesProcessor filesProcessor = new FilesProcessor();
        filesProcessor.process();
        log.info("Execution Completed in: {} ms.", Duration.between(start, Instant.now()).toMillis());
    }

}