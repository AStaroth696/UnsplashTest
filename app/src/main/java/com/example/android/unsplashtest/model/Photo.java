package com.example.android.unsplashtest.model;

public class Photo {
    private String thumbNailUrl;
    private String regularUrl;
    private String id;
    private String downloadUrl;

    public Photo(String thumbNailUrl, String regularUrl, String id, String downloadUrl) {
        this.thumbNailUrl = thumbNailUrl;
        this.regularUrl = regularUrl;
        this.id = id;
        this.downloadUrl = downloadUrl;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
