package com.tagmp3.service;

import com.tagmp3.Application;
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
import java.net.URLEncoder;

import static com.tagmp3.util.Constants.FILENAME_ARTIST_TRACK_SEPARATOR;
import static com.tagmp3.util.Constants.GOOGLE_SEARCH_ENCODE;
import static com.tagmp3.util.Constants.GOOGLE_SEARCH_MAGIC_CLASS;
import static com.tagmp3.util.Constants.GOOGLE_SEARCH_URL;
import static com.tagmp3.util.Constants.GOOGLE_SEARCH_URL_SEARCH_PARAM;
import static com.tagmp3.util.Constants.MP3_FILE_EXTENSION;
import static com.tagmp3.util.Constants.MP3_PROCESSED_COMMENT;

public class FileTagService {

    final static Logger log = LogManager.getLogger(Application.class);

    public void processFile(File file, String genre) {
        if (!file.getName().contains(MP3_FILE_EXTENSION)) {
            return;
        }
        try {
            AudioFile mp3 = AudioFileIO.read(file);
            AbstractID3v2Tag oldTag = ((MP3File) mp3).getID3v2Tag();

            if (MP3_PROCESSED_COMMENT.equals(oldTag.getFirst(FieldKey.COMMENT))){
                return;
            }

            ID3v23Tag newTag = generateNewTag(file, genre, oldTag);

            AudioFileIO.delete(mp3);
            mp3.setTag(newTag);
            AudioFileIO.write(mp3);

        } catch (Exception e) {
            log.error("ERROR occurred trying to process: {}", file.getName());
        }

    }

    private ID3v23Tag generateNewTag(File file, String genre, Tag oldTag) {
        log.info("Processing: {}", file);
        String fileName = file.getName().replace(MP3_FILE_EXTENSION, "");
        ID3v23Tag newTag = new ID3v23Tag();
        try {
            newTag.setField(FieldKey.ARTIST, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[0].trim());
            newTag.setField(FieldKey.TITLE, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[1].trim());
            newTag.setField(FieldKey.ALBUM, fileName.split(FILENAME_ARTIST_TRACK_SEPARATOR)[0].trim());
            newTag.setField(FieldKey.GENRE, genre);
            newTag.setField(oldTag.getFirstArtwork());
            newTag.deleteField(FieldKey.TRACK);
            newTag.setField(FieldKey.COMMENT, MP3_PROCESSED_COMMENT);

            String yearFromGoogle = getYearFromGoogle(fileName);

            if (yearFromGoogle != null) {
                newTag.setField(FieldKey.YEAR, yearFromGoogle);
            } else {
                newTag.setField(FieldKey.YEAR, oldTag.getFirst(FieldKey.YEAR));
            }

        } catch (Exception e) {
            log.error("ERROR occurred trying to generate new tags for: {}", file.getName());
        }
        return newTag;
    }


    private String getYearFromGoogle(String songName) {
        try {
            String url = GOOGLE_SEARCH_URL + URLEncoder.encode(songName + GOOGLE_SEARCH_URL_SEARCH_PARAM, GOOGLE_SEARCH_ENCODE);
            Document doc = Jsoup.connect(url).get();

            return doc.getElementsByClass(GOOGLE_SEARCH_MAGIC_CLASS).get(0).childNode(0).toString().trim();
        } catch (Exception e) {
            log.error("ERROR occurred trying to get Year tag from Google for: {}", songName);
            return null;
        }
    }

}
