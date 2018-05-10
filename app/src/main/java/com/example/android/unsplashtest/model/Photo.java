package com.example.android.unsplashtest.model;

public class Photo {
    private String thumbNailUrl;
    private String regularUrl;
    private String id;

    public Photo(String thumbNailUrl, String regularUrl, String id) {
        this.thumbNailUrl = thumbNailUrl;
        this.regularUrl = regularUrl;
        this.id = id;
    }

    public String getThumbNailUrl() {
        return thumbNailUrl;
    }

    public String getRegularUrl() {
        return regularUrl;
    }

    public String getId() {
        return id;
    }
}
