package com.example.android.unsplashtest.networking;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Retrofit service for authentication
 */
public interface AuthService {
    @FormUrlEncoded
    @POST("oauth/token")
    Call<JsonElement> authenticate(@Field("client_id") String clientId,
                                   @Field("client_secret") String clientSecret,
                                   @Field("redirect_uri") String redirectUri,
                                   @Field("code") String code,
                                   @Field("grant_type") String grantType);
}
