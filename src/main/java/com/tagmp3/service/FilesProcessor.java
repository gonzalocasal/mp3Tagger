package com.tagmp3.service;

import com.tagmp3.out.ResultsWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.tagmp3.util.Constants.MP3_FILE_EXTENSION;

@Log4j2
@Component
public class FilesProcessor {

    private final String rootFolder;
    private final String errorOutPutFile;
    private final List<String> genresFolders;
    private final ResultsWriter resultsWriter;
    private final FileTagService fileTagService;

    public FilesProcessor(@Value("${root.folder}") String rootFolder,
                          @Value("${error.output.file}") String errorOutPutFile,
                          @Value("#{'${genres.folders}'.split(',')}") List<String> genresFolders,
                          @Autowired ResultsWriter resultsWriter,
                          @Autowired FileTagService fileTagService) {
        this.rootFolder = rootFolder;
        this.errorOutPutFile = errorOutPutFile;
        this.genresFolders = genresFolders;
        this.resultsWriter = resultsWriter;
        this.fileTagService = fileTagService;
    }

    public void process () {
        Instant start = Instant.now();
        StringBuilder errorLines = new StringBuilder();

        for (String genre : genresFolders) {
            log.info("Processing: " + genre);
            File dir = new File(rootFolder + genre);
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getName().contains(MP3_FILE_EXTENSION)) {
                        try {
                            fileTagService.processFile(file, genre, errorLines);
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            } else {
                log.error(String.format("Error: Files not found at: %s", dir.getAbsolutePath()));
            }
        }
        resultsWriter.export(errorOutPutFile, errorLines);
        log.info("Execution Completed in: " + Duration.between(start, Instant.now()));
    }

}
