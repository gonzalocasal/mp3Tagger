package com.tagmp3;

import com.tagmp3.com.tagmp3.service.YearTagResolver;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    private static final Logger LOGGER = Logger.getLogger("Application");
    private static final String MP3_FILE_EXTENSION = ".mp3";
    private static final String PATH = "/Users/gonzalo/Archivos/Música/";

    public static void main(String[] args) throws Exception {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        long time1 = System.nanoTime();

        List<String> genres = Arrays.asList("Cumbia", "Dance", "Folklore", "Pop", "Pop Latino", "Reggae", "Retro", "Rock", "Rock Latino");

        for (String genre : genres) {

            LOGGER.info("Processing: " + genre);

            File dir = new File(PATH + genre);
            File[] originalFiles = dir.listFiles();

            for (File file : Objects.requireNonNull(originalFiles)) {
                if (file.getName().contains(MP3_FILE_EXTENSION)) {
                    try {
                        processFile(file, genre);
                    } catch (Exception e) {
                        LOGGER.info("ERROR With File: " + file.getName());
                    }
                }
            }
        }

        long time2 = System.nanoTime();
        long timeTaken = time2 - time1;
        double seconds = (double) timeTaken / 1_000_000_000.0;
        LOGGER.info("Task completed in: " + seconds+ " seconds");
    }



    private static void processFile(File file, String genre) throws Exception {

        String songName = file.getName().replace(MP3_FILE_EXTENSION, "");
        AudioFile mp3 = AudioFileIO.read(file);
        Tag oldTag = mp3.getTag();

        if (genre.equals(oldTag.getFirst(FieldKey.GENRE)) && oldTag.getFirst(FieldKey.TRACK).isEmpty()){
            return;
        }

        LOGGER.info("Processing: " + file);

        ID3v23Tag newTag = new ID3v23Tag();

        newTag.setField(FieldKey.ARTIST, songName.split(" - ")[0].trim());
        newTag.setField(FieldKey.TITLE, songName.split(" - ")[1].trim());
        newTag.setField(FieldKey.ALBUM, songName.split(" - ")[0].trim());
        newTag.setField(FieldKey.GENRE, genre);
        newTag.setField(oldTag.getFirstArtwork());
        newTag.deleteField(FieldKey.TRACK);

        try{
            newTag.setField(FieldKey.YEAR,YearTagResolver.getYear(songName));
        } catch (Exception e){
            LOGGER.info("ERROR occurred trying to get Year tag on: " + file.getName());
        }

        AudioFileIO.delete(mp3);

        mp3.setTag(newTag);
        AudioFileIO.write(mp3);
    }

}