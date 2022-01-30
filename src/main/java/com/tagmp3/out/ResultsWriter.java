package com.tagmp3.out;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Generic class to writes Games data into a flat File.
 */
@Log4j2
@Component
@AllArgsConstructor
public class ResultsWriter {

    public void export (String outPath, StringBuilder lines) {
        log.info("Writing the file {}", outPath);
        Path path = Paths.get(outPath);
        String linesStr = lines.toString();
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(linesStr);
        } catch (IOException e) {
            throw new RuntimeException(e.getCause());
        }
        log.info("File write complete. {} lines written.", linesStr.lines().count());
    }

}
