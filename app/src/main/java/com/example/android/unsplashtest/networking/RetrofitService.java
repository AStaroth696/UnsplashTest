package com.example.android.unsplashtest.networking;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("photos")
    Call<JsonElement> getPhotosList(@Header("Authorization") String header, @Query("page") int page);

    @GET("search/photos")
    Call<JsonElement> searchPhotos(@Header("Authorization") String header, @Query("page") int page,
                                   @Query("query") String query);
}
