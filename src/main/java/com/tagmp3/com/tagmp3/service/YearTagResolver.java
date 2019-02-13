package com.tagmp3.com.tagmp3.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URLEncoder;

public class YearTagResolver  {

    private static final String GOOGLE_URL = "https://www.google.com/search?q=";
    private static final String MAGIC_CLASS = "Z0LcW";
    private static final String URL_SEARCH_PARAM = " cancion fecha de lanzamiento";

    public static String getYear(String songName) throws IOException{
        String url = GOOGLE_URL + URLEncoder.encode(songName + URL_SEARCH_PARAM, "UTF-8");
        Document doc = Jsoup.connect(url).get();
        return doc.getElementsByClass(MAGIC_CLASS).get(0).childNode(0).toString().trim();
    }

}
