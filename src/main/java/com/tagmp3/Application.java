package com.tagmp3;

import com.mpatric.mp3agic.*;
import com.tagmp3.file.FileParser;
import com.tagmp3.web.GoogleSearcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class Application {

    private static final Logger logger = LogManager.getLogger(Application.class);

    @Autowired
    FileParser fileParser;

    @Autowired
    GoogleSearcher googleSearcher;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            File dir = new File("./music");
            File[] filesList = dir.listFiles();
            for (File file : filesList) {
                String songName = fileParser.parseSongName(file.getName());
                String songYear = googleSearcher.findSongYear(songName);
                tagFile(file, songYear);
            }
            logger.info("TASK COMPLETED");

        };
    }

    private void tagFile(File file, String songYear) throws IOException, UnsupportedTagException, InvalidDataException, NotSupportedException {
        if (songYear.isEmpty())
            return;
        Mp3File mp3file = new Mp3File(file.getAbsolutePath());
        ID3v1 id3v1Tag  =  mp3file.getId3v1Tag();
        ID3v2 id3v2Tag = mp3file.getId3v2Tag();
        id3v1Tag.setYear(songYear);
        id3v2Tag.setYear(songYear);
        mp3file.setId3v1Tag(id3v1Tag);
        mp3file.setId3v2Tag(id3v2Tag);
        mp3file.save(file.getName());
    }

}
