package com.tagmp3;

import com.mpatric.mp3agic.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Application {

    private final static Logger LOGGER = Logger.getLogger("Application");

    public static void main(String[] args) throws UnsupportedTagException, NotSupportedException, InvalidDataException, IOException {
        File dir = new File(".");
        File out = new File("result/");
        out.mkdir();
        File[] originalFiles = dir.listFiles();
        readFolder(originalFiles);
        LOGGER.info("TASK COMPLETED");
    }
    private static void readFolder(File[] originalFiles) throws InvalidDataException, IOException, UnsupportedTagException, NotSupportedException {
        for (File file : originalFiles) {
            if (file.getName().contains(".mp3")) {
                Mp3File mp3 = new Mp3File(file.getAbsolutePath());
                ID3v2 tag = mp3.getId3v2Tag();
                LOGGER.info(file.getName());
                appy(mp3, tag, file.getName());
            }
        }
    }


    private static void appy(Mp3File mp3, ID3v2 oldTag, String fileName) throws IOException, NotSupportedException {
        String songName = fileName.replace(".mp3", "");
        String artist = songName.split("-")[0].trim();
        String title = songName.split("-")[1].trim();
        String year = oldTag.getYear();
        byte[] albumImage = oldTag.getAlbumImage();
        String mimeType = oldTag.getAlbumImageMimeType();
        ID3v23Tag newTag = new ID3v23Tag();
        newTag.setArtist(artist);
        newTag.setTitle(title);
        newTag.setAlbum(title);
        newTag.setYear(year);
        newTag.setAlbumImage(albumImage,mimeType);
        mp3.removeId3v1Tag();
        mp3.setId3v2Tag(newTag);
        mp3.save("result/"+fileName);
    }
}