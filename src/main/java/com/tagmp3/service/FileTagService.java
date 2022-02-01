package com.tagmp3.service;

import lombok.extern.log4j.Log4j2;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.tagmp3.util.Constants.FILENAME_ARTIST_TRACK_SEPARATOR;
import static com.tagmp3.util.Constants.MP3_FILE_EXTENSION;
import static com.tagmp3.util.Constants.MP3_PROCESSED_COMMENT;

@Log4j2
@Component
public class FileTagService {

    private final YearTagResolver yearTagResolver;

    public FileTagService (@Autowired YearTagResolver yearTagResolver) {
        this.yearTagResolver = yearTagResolver;
    }

    public void processFile(File file, String genre, StringBuilder errorLines) throws Exception {
        String fileName = file.getName().replace(MP3_FILE_EXTENSION, "");
        AudioFile mp3 = AudioFileIO.read(file);
        AbstractID3v2Tag oldTag = ((MP3File) mp3).getID3v2Tag();
        if (MP3_PROCESSED_COMMENT.equals(oldTag.getFirst(FieldKey.COMMENT))){
            return;
        }

        log.info(String.format("Processing: %s", file));
        ID3v23Tag newTag = generateNewTag(file, genre, fileName, oldTag, errorLines);
        replaceTag(mp3, newTag);
    }

    private ID3v23Tag generateNewTag(File file, String genre, String fileName, Tag oldTag, StringBuilder errorLines) throws Exception {
        ID3v23Tag newTag = new ID3v23Tag();
        newTag.setField(FieldKey.ARTIST, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[0].trim());
        newTag.setField(FieldKey.TITLE, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[1].trim());
        newTag.setField(FieldKey.ALBUM, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[0].trim());
        newTag.setField(FieldKey.GENRE, genre);
        newTag.setField(oldTag.getFirstArtwork());
        newTag.deleteField(FieldKey.TRACK);
        newTag.setField(FieldKey.COMMENT, MP3_PROCESSED_COMMENT);

        try{
            newTag.setField(FieldKey.YEAR, yearTagResolver.getYear(fileName));
        } catch (Exception e){
            newTag.setField(FieldKey.YEAR, oldTag.getFirst(FieldKey.YEAR));

            String yearTagError = String.format("ERROR occurred trying to get Year tag on: %s", file.getName());
            errorLines.append(yearTagError);
            errorLines.append(System.lineSeparator());
            log.error(yearTagError);
        }
        return newTag;
    }

    private void replaceTag(AudioFile mp3, ID3v23Tag newTag) throws CannotReadException, CannotWriteException {
        AudioFileIO.delete(mp3);
        mp3.setTag(newTag);
        AudioFileIO.write(mp3);
    }

}
