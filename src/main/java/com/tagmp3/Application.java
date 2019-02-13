package com.tagmp3;

import com.tagmp3.com.tagmp3.service.YearTagResolver;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    private static final Logger LOGGER = Logger.getLogger("Application");
    private static final String MP3_FILE_EXTENSION = ".mp3";

    public static void main(String[] args)  {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        long time1 = System.nanoTime();
        File dir = new File(".");
        File[] originalFiles = dir.listFiles();
        for (File file : Objects.requireNonNull(originalFiles))
            readFolder(file);
        long time2 = System.nanoTime();
        long timeTaken = time2 - time1;
        double seconds = (double) timeTaken / 1_000_000_000.0;
        LOGGER.info("Task completed in: " + seconds+ " seconds");
    }

    private static void readFolder(File file) {
        try{
            if (file.getName().contains(MP3_FILE_EXTENSION))
                processFile(file);
        }catch (Exception e){
            LOGGER.info("Error ocurred trying to apply changes on: " + file.getName());
        }
    }

    private static void processFile(File file) throws Exception {
        AudioFile mp3 = AudioFileIO.read(file);
        Tag oldTag = mp3.getTag();
        ID3v23Tag newTag = new ID3v23Tag();

        String songName = file.getName().replace(MP3_FILE_EXTENSION, "");

        newTag.setField(FieldKey.ARTIST,songName.split(" - ")[0].trim());
        newTag.setField(FieldKey.TITLE,songName.split(" - ")[1].trim());
        newTag.setField(FieldKey.ALBUM,songName);
        newTag.setField(FieldKey.YEAR,YearTagResolver.getYear(songName));
        newTag.setField(FieldKey.GENRE,oldTag.getFirst(FieldKey.GENRE));
        newTag.setField(oldTag.getFirstArtwork());
        newTag.deleteField(FieldKey.TRACK);

        AudioFileIO.delete(mp3);

        mp3.setTag(newTag);
        AudioFileIO.write(mp3);
    }

}