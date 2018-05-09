package com.example.android.unsplashtest.presenter;

import android.widget.ImageView;

import com.example.android.unsplashtest.activity.DetailActivity;

import com.squareup.picasso.Picasso;

public class DetailPresenter{
    private DetailActivity context;

    public DetailPresenter(DetailActivity detailActivity) {
        this.context = detailActivity;
    }

    public void loadPhotoIntoContainer(ImageView photo, String url){
        Picasso.with(context).load(url).into(photo);
    }
}
