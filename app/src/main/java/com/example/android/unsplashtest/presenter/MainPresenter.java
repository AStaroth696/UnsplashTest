package com.example.android.unsplashtest.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.android.unsplashtest.R;
import com.example.android.unsplashtest.activity.DetailActivity;
import com.example.android.unsplashtest.activity.MainActivity;
import com.example.android.unsplashtest.model.Photo;
import com.example.android.unsplashtest.networking.AuthService;
import com.example.android.unsplashtest.networking.DaggerNetworkComponent;
import com.example.android.unsplashtest.networking.NetworkComponent;
import com.example.android.unsplashtest.networking.NetworkModule;
import com.example.android.unsplashtest.networking.RetrofitService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainPresenter {
    @Inject
    Retrofit retrofit;
    private AuthService authService;
    private RetrofitService service;
    private MainActivity context;
    private List<Photo> photos;
    private String token;
    private boolean loading;
    private String query;
    private boolean searching;

    public MainPresenter(MainActivity mainActivity) {
        this.context = mainActivity;
        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule("https://unsplash.com/"))
                .build();
        networkComponent.inject(this);
        authService = retrofit.create(AuthService.class);

        networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule("https://api.unsplash.com/"))
                .build();
        networkComponent.inject(this);
        service = retrofit.create(RetrofitService.class);

        photos = new ArrayList<>();
    }

    public void processWebView(final WebView webView) {
        webView.setVisibility(View.VISIBLE);
        String url = context.getResources().getString(R.string.authentication_url) + "?"
                + "client_id=" + context.getResources().getString(R.string.client_id) + "&"
                + "redirect_uri=" + context.getResources().getString(R.string.redirect_uri) + "&"
                + "response_type=" + context.getResources().getString(R.string.response_type) + "&"
                + "scope=" + context.getResources().getString(R.string.scope);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("oauth/authorize") && !url.contains("&")) {
                    int index = url.lastIndexOf("/") + 1;
                    String code = url.substring(index);
                    Log.d("AUTHORIZE", "code: " + code);
                    webView.setVisibility(View.INVISIBLE);
                    authorize(code);
                }
                webView.loadUrl(url);
                return false;
            }
        });
    }

    private void authorize(final String code) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<JsonElement> call = authService.authenticate(
                        context.getResources().getString(R.string.client_id),
                        context.getResources().getString(R.string.client_secret),
                        context.getResources().getString(R.string.redirect_uri),
                        code,
                        context.getResources().getString(R.string.auth_grant_type)
                );

                try {
                    Response<JsonElement> response = call.execute();
                    token = response.body().getAsJsonObject().get("access_token").getAsString();
                    Log.d("AUTHORIZE", "token: " + token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loadPhotos();
            }
        }.execute();
    }

    public void receivePhotos() {
        if (searching) {
            searchPhotos();
        } else {
            loadPhotos();
        }

    }

    public void loadPhotos() {
        loading = true;
        context.setProgressBarVisible();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<JsonElement> call = service.getPhotosList("Bearer " + token,
                        (photos.size() / 10) + 1);
                Log.d("PHOTOS", "get photos request: " + call.request().toString() + " : " + call.request().headers().toString());
                try {
                    Response<JsonElement> response = call.execute();
                    JsonArray jsonArray = response.body().getAsJsonArray();
                    for (JsonElement jsonElement : jsonArray) {
                        getPhotoFromJson(jsonElement);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loading = false;
                context.notifyAdapter();
                context.setProgressBarInvisible();
            }
        }.execute();
    }

    public void searchPhotos() {
        loading = true;
        context.setProgressBarVisible();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<JsonElement> call = service.searchPhotos("Bearer " + token,
                        (photos.size() / 10) + 1, query);
                Log.d("PHOTOS", "search photos request: " + call.request().toString() + " : " + call.request().headers().toString());
                try {
                    Response<JsonElement> response = call.execute();
                    JsonArray jsonArray = response.body().getAsJsonObject().get("results").getAsJsonArray();
                    for (JsonElement jsonElement : jsonArray) {
                        getPhotoFromJson(jsonElement);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                loading = false;
                context.notifyAdapter();
                context.setProgressBarInvisible();
            }
        }.execute();
    }

    public void bindView(ImageView photo, int position) {
        Picasso.with(context).load(photos.get(position).getThumbNailUrl()).into(photo);
    }

    public void itemClicked(Photo photo) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.PHOTO_ID, photo.getId());
        intent.putExtra(DetailActivity.PHOTO_URL, photo.getRegularUrl());
        context.startActivity(intent);
    }

    public List<Photo> getPhotoList() {
        return photos;
    }

    private void getPhotoFromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        JsonObject downloadJsonObject = jsonObject.get("links").getAsJsonObject();
        String downloadUrl = downloadJsonObject.get("download").getAsString();
        jsonObject = jsonObject.get("urls").getAsJsonObject();
        String thumbnailUrl = jsonObject.get("thumb").getAsString();
        String regularUrl = jsonObject.get("regular").getAsString();
        photos.add(new Photo(thumbnailUrl, regularUrl, id, downloadUrl));
    }

    public void clearPhotoList() {
        photos.clear();
    }

    public boolean isLoading() {
        return loading;
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public boolean isSearching() {
        return searching;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}