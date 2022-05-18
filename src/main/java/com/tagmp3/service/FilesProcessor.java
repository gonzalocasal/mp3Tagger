package com.tagmp3.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.tagmp3.util.Constants.GENRES_FOLDERS;
import static com.tagmp3.util.Constants.ROOT_FOLDER;

public class FilesProcessor {

    static final Logger log = LogManager.getLogger(FilesProcessor.class);

    private final FileTagService fileTagService;

    public FilesProcessor() {
        this.fileTagService = new FileTagService();
    }

    public void process () {
        GENRES_FOLDERS.parallelStream().forEach(genre -> {
            log.info("Processing: {}", genre);
            File dir = new File(ROOT_FOLDER + genre);
            List<File> files = Arrays.asList(Objects.requireNonNull(dir.listFiles()));
            files.parallelStream().forEach(file -> fileTagService.processFile(file, genre));
        });
    }

}