package com.example.android.unsplashtest.networking;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("photos")
    Call<JsonElement> getPhotosList(@Header("Authorization") String header, @Query("page") int page,
                                    @Query("per_page")int perPage);

    @GET("search/photos")
    Call<JsonElement> searchPhotos(@Header("Authorization") String header, @Query("page") int page,
                                   @Query("per_page")int perPage, @Query("query") String query);
}
