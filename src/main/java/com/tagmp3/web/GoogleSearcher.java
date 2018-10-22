package com.tagmp3.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class GoogleSearcher {

    private static final Logger logger = LogManager.getLogger(GoogleSearcher.class);

    @Autowired
    private HtmlReader htmlReader;
    private RestTemplate restTemplate = new RestTemplate();

    public String findSongYear(String songName) {
        try {
            String fooResourceUrl = "https://www.google.com.ar/search?q="+ songName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "eltabo");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response =  restTemplate.exchange(fooResourceUrl, HttpMethod.GET, entity,String.class);
            return  htmlReader.findYearInHtml(response.getBody(), songName);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



}
