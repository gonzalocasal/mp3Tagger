package com.tagmp3.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static com.tagmp3.util.Constants.*;

public class FileTagService {

    private static final Logger log = LogManager.getLogger(FileTagService.class);

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public void processFile(File file, String genre) {
        if (!file.getName().contains(MP3_FILE_EXTENSION)) {
            return;
        }
        try {
            AudioFile mp3 = AudioFileIO.read(file);
            AbstractID3v2Tag oldTag = ((MP3File) mp3).getID3v2Tag();

            if (MP3_PROCESSED_COMMENT.equals(oldTag.getFirst(FieldKey.ENGINEER))){
                return;
            }
            log.info("Processing: {}", file);
            ID3v23Tag newTag = generateNewTag(file, genre, oldTag);

            AudioFileIO.delete(mp3);
            mp3.setTag(newTag);
            AudioFileIO.write(mp3);

        } catch (Exception e) {
            log.error("ERROR occurred trying to process: {}", file.getName());
        }
    }

    private ID3v23Tag generateNewTag(File file, String genre, Tag oldTag) throws Exception {
        String fileName = file.getName().replace(MP3_FILE_EXTENSION, "");
        ID3v23Tag newTag = new ID3v23Tag();
        try {
            newTag.setField(FieldKey.ARTIST, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[0].trim());
            newTag.setField(FieldKey.TITLE, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[1].trim());
            newTag.setField(FieldKey.ALBUM, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[0].trim());
            newTag.setField(FieldKey.GENRE, genre);
            newTag.setField(FieldKey.ENGINEER, MP3_PROCESSED_COMMENT);
            newTag.setField(FieldKey.YEAR, getYearFromGoogle(fileName).orElse(oldTag.getFirst(FieldKey.YEAR)));
            newTag.setField(FieldKey.COMMENT, getCreationDate(file));
            newTag.setField(oldTag.getFirstArtwork());
        } catch (Exception e) {
            log.error("ERROR occurred trying to generate new tags for: {}", file.getName());
            throw e;
        }
        return newTag;
    }

    private String getCreationDate(File file) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(Path.of(file.getPath()), BasicFileAttributes.class);
        Date creationDate = new Date(attr.creationTime().toMillis());
        return formatter.format(creationDate);
    }

    private Optional<String> getYearFromGoogle(String songName) {
        try {
            String url = GOOGLE_SEARCH_URL + URLEncoder.encode(songName + GOOGLE_SEARCH_URL_SEARCH_PARAM, GOOGLE_SEARCH_ENCODE);
            Document doc = Jsoup.connect(url).get();
            return Optional.of(doc.getElementsByClass(GOOGLE_SEARCH_MAGIC_CLASS).get(0).childNode(0).toString().trim());
        } catch (Exception e) {
            log.error("ERROR occurred trying to get Year tag from Google for: {}", songName);
            return Optional.empty();
        }
    }

}
