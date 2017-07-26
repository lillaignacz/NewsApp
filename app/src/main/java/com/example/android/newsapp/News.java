package com.example.android.newsapp;

/**
 * Created by lilla on 25/07/17.
 */

public class News {

    String title;
    String sectionName;
    String webUrl;

    public News(String title, String sectionName, String webUrl) {
        this.title = title;
        this.sectionName = sectionName;
        this.webUrl = webUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebUrl() {
        return webUrl;
    }

}
