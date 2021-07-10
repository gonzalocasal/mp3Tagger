package com.tagmp3.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

import static com.tagmp3.util.Constants.GOOGLE_SEARCH_ENCODE;
import static com.tagmp3.util.Constants.GOOGLE_SEARCH_MAGIC_CLASS;
import static com.tagmp3.util.Constants.GOOGLE_SEARCH_URL;
import static com.tagmp3.util.Constants.GOOGLE_SEARCH_URL_SEARCH_PARAM;

@Component
public class YearTagResolver  {

    public String getYear(String songName) throws IOException{
        String url = GOOGLE_SEARCH_URL + URLEncoder.encode(songName + GOOGLE_SEARCH_URL_SEARCH_PARAM, GOOGLE_SEARCH_ENCODE);
        Document doc = Jsoup.connect(url).get();
        return doc.getElementsByClass(GOOGLE_SEARCH_MAGIC_CLASS).get(0).childNode(0).toString().trim();
    }

}
