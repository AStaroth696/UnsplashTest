package com.example.android.unsplashtest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{
    private String thumbNailUrl;
    private String regularUrl;
    private String id;

    public Photo(String thumbNailUrl, String regularUrl, String id) {
        this.thumbNailUrl = thumbNailUrl;
        this.regularUrl = regularUrl;
        this.id = id;
    }


    protected Photo(Parcel in) {
        thumbNailUrl = in.readString();
        regularUrl = in.readString();
        id = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getThumbNailUrl() {
        return thumbNailUrl;
    }

    public String getRegularUrl() {
        return regularUrl;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(thumbNailUrl);
        parcel.writeString(regularUrl);
        parcel.writeString(id);
    }
}
