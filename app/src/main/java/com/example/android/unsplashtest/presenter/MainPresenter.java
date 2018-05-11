package com.example.android.unsplashtest.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.android.unsplashtest.networking.NetworkStateInfo;
import com.example.android.unsplashtest.networking.RetrofitService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Presenter class for main activity
 */
public class MainPresenter {
    @Inject
    Retrofit retrofit;
    private AuthService authService;
    private RetrofitService service;
    private MainActivity context;
    private List<Photo> photos;
    private Set<String> calbacks;
    private String token;
    private String query;
    private boolean searching;
    private static final String PHOTO_LIST = "photo list";
    private static final String TOKEN = "token";

    public MainPresenter(MainActivity mainActivity) {
        this.context = mainActivity;
        //Create Retrofit service for authentication
        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule("https://unsplash.com/"))
                .build();
        networkComponent.inject(this);
        authService = retrofit.create(AuthService.class);

        //Create Retrofit service for loading and searching photos
        networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule("https://api.unsplash.com/"))
                .build();
        networkComponent.inject(this);
        service = retrofit.create(RetrofitService.class);

        photos = new ArrayList<>();
        calbacks = new HashSet<>();
    }

    //Start process to receive code for authentication
    public void processWebView(final WebView webView) {
        if (NetworkStateInfo.isNetworkAvailable(context)) {
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
                        webView.setVisibility(View.INVISIBLE);
                        //Start authorization after code received
                        authorize(code);
                    }
                    webView.loadUrl(url);
                    return false;
                }
            });
        }
    }

    //Authorization process to receive token
    private void authorize(final String code) {
        if (NetworkStateInfo.isNetworkAvailable(context)) {
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
    }

    //Load usual photos or load photos by query depending on flag
    public void receivePhotos() {
        if (searching) {
            searchPhotos();
        } else {
            loadPhotos();
        }

    }

    //Load usual photos
    public void loadPhotos() {
        if (NetworkStateInfo.isNetworkAvailable(context)) {
            context.setProgressBarVisible();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Call<JsonElement> call = service.getPhotosList("Bearer " + token,
                            (photos.size() / 20) + 1, 20);
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
                    context.notifyAdapter();
                    context.setProgressBarInvisible();
                }
            }.execute();
        }
    }

    //Load photos by search query
    public void searchPhotos() {
        if (NetworkStateInfo.isNetworkAvailable(context)) {
            context.setProgressBarVisible();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Call<JsonElement> call = service.searchPhotos("Bearer " + token,
                            (photos.size() / 20) + 1, 20, query);
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
                    context.notifyAdapter();
                    context.setProgressBarInvisible();
                }
            }.execute();
        }
    }

    //Bind card in Grid layout
    public void bindView(final ImageView photo, final int position) {
        if (NetworkStateInfo.isNetworkAvailable(context)) {
            Picasso.with(context).load(photos.get(position).getThumbNailUrl()).into(photo, new Callback() {
                @Override
                public void onSuccess() {
                    calbacks.add(photos.get(position).getId());
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    //Grid item clicked processing
    public void itemClicked(Photo photo) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.PHOTO_ID, photo.getId());
        intent.putExtra(DetailActivity.PHOTO_URL, photo.getRegularUrl());
        context.startActivity(intent);
    }

    public List<Photo> getPhotoList() {
        return photos;
    }

    public void setPhotoList(Photo[] photoArray){
        photos.addAll(Arrays.asList(photoArray));
    }

    //Convert json element to Photo object
    private void getPhotoFromJson(JsonElement jsonElement) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String id = jsonObject.get("id").getAsString();
        jsonObject = jsonObject.get("urls").getAsJsonObject();
        String thumbnailUrl = jsonObject.get("thumb").getAsString();
        String regularUrl = jsonObject.get("regular").getAsString();
        photos.add(new Photo(thumbnailUrl, regularUrl, id));
    }

    //Remove data from photos and callbacks list when search starts or ends
    public void clearPhotoList() {
        photos.clear();
        calbacks.clear();
    }

    public void setToken(String token) {
        this.token = token;
    }

    //Flag for permission to load a new portion of photos
    public boolean isLoaded() {
        return !(photos.size() > calbacks.size());
    }

    //Flag for changing receiving photos mode
    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public boolean isSearching() {
        return searching;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    //Save instance state processing
    public void onSaveState(Bundle outState){
        //Convert photos list into parcelable array
        Parcelable[] parcelables = new Parcelable[getPhotoList().size()];
        for (int i = 0; i < getPhotoList().size(); i++){
            parcelables[i] = getPhotoList().get(i);
        }
        outState.putParcelableArray(PHOTO_LIST ,parcelables);
        outState.putString(TOKEN, token);
    }

    //Restore instance state processing
    public void onRestoreState(Bundle savedInstanceState){
        //Get parcelable array from bundle and convert to Photo object array
        Parcelable[] parcelables = savedInstanceState.getParcelableArray(PHOTO_LIST);
        Photo[] photos = new Photo[parcelables.length];
        for (int i = 0; i <parcelables.length; i++){
            photos[i] = (Photo) parcelables[i];
        }
        setPhotoList(photos);
        setToken(savedInstanceState.getString(TOKEN));
    }
}
