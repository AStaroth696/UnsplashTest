package com.example.android.unsplashtest.networking;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Retrofit service for loading and searching photos
 */
public interface RetrofitService {
    //Load photos
    @GET("photos")
    Call<JsonElement> getPhotosList(@Header("Authorization") String header, @Query("page") int page,
                                    @Query("per_page")int perPage);
    //Search photos by query parameter
    @GET("search/photos")
    Call<JsonElement> searchPhotos(@Header("Authorization") String header, @Query("page") int page,
                                   @Query("per_page")int perPage, @Query("query") String query);
}
