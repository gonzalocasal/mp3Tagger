package com.tagmp3.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class HtmlReader {

    private static final Logger logger = LogManager.getLogger(HtmlReader.class);

    public String findYearInHtml(String html, String songName) {
        try{
            Document doc = Jsoup.parse(html);
            Elements elements = doc.getElementsContainingOwnText("Fecha de lanzamiento");
            String year = (elements.get(0).parentNode().childNode(1).childNode(0)).toString();
            logger.info(year);
            return year;
        }
        catch(Exception exception){
            logger.error("Year not found for song: " + songName);
            return "";
        }
    }

    public String findAlbumInHtml(String html, String songName) {
        try{
            Document doc = Jsoup.parse(html);
            Elements elements = doc.getElementsContainingOwnText("Álbum:");

            String album = (elements.get(0).parentNode().childNode(1).childNode(0).childNode(0)).toString();
            logger.info(album);
            return album;
        }
        catch(Exception exception){
            logger.error("Album tag not found for song: " + songName);
            return "";
        }
    }


}
