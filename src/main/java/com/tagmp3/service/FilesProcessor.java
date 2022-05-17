package com.tagmp3.service;

import com.tagmp3.out.ResultsWriter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        StringBuilder errorLines = new StringBuilder();

        genresFolders.parallelStream().forEach(genre -> {
            log.info("Processing: {}", genre);
            File dir = new File(rootFolder + genre);
            List<File> files = Arrays.asList(Objects.requireNonNull(dir.listFiles()));
            files.forEach(file -> {
                fileTagService.processFile(file, genre, errorLines);
            });
        });

        resultsWriter.export(errorOutPutFile, errorLines);
    }

}
