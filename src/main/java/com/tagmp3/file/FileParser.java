package com.tagmp3.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class FileParser {

    private static final Logger logger = LogManager.getLogger(FileParser.class);

    public String parseSongName (String fileName){
        String songName = fileName.replace(".mp3", "");
        logger.info(songName);
        return songName;
    }


}
