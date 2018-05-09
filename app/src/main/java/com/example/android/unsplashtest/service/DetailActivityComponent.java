package com.example.android.unsplashtest.service;

import com.example.android.unsplashtest.activity.DetailActivity;

import dagger.Component;

@Component(modules = {DetailActivityModule.class})
public interface DetailActivityComponent {
    public void inject(DetailActivity detailActivity);
}
