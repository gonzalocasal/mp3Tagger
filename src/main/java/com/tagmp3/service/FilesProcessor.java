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

    private final String rootFolder;
    private final List<String> genresFolders;
    private final FileTagService fileTagService;

    public FilesProcessor() {
        this.rootFolder = ROOT_FOLDER;
        this.genresFolders = GENRES_FOLDERS;
        this.fileTagService = new FileTagService();
    }

    public void process () {
        genresFolders.parallelStream().forEach(genre -> {
            log.info("Processing: {}", genre);
            File dir = new File(rootFolder + genre);
            List<File> files = Arrays.asList(Objects.requireNonNull(dir.listFiles()));
            files.parallelStream().forEach(file -> fileTagService.processFile(file, genre));
        });
    }

}