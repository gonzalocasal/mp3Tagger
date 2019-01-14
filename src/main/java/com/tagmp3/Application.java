package com.tagmp3;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public class Application {

    private static final Logger LOGGER = Logger.getLogger("Application");
    private static final String MP3_FILE_EXTENSION = ".mp3";

    public static void main(String[] args)  {
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
        String songName = file.getName().replace(MP3_FILE_EXTENSION, "");
        String artist = songName.split(" - ")[0].trim();
        String title = songName.split(" - ")[1].trim();
        String year = oldTag.getFirst(FieldKey.YEAR);
        String genre = oldTag.getFirst(FieldKey.GENRE);
        Artwork artwork = oldTag.getFirstArtwork();
        applyChanges(mp3, songName, artist, title, year, genre, artwork);
    }

    private static void applyChanges(AudioFile mp3, String songName, String artist, String title, String year, String genre, Artwork artwork) throws Exception  {
        ID3v23Tag newTag = new ID3v23Tag();
        newTag.setField(FieldKey.ARTIST,artist);
        newTag.setField(FieldKey.TITLE,title);
        newTag.setField(FieldKey.ALBUM,songName);
        newTag.setField(FieldKey.YEAR,year);
        newTag.setField(FieldKey.GENRE,genre);
        newTag.setField(artwork);
        mp3.setTag(newTag);
        AudioFileIO.write(mp3);
    }
}