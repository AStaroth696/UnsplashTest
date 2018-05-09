package com.example.android.unsplashtest.service;

import com.example.android.unsplashtest.activity.MainActivity;

import dagger.Component;

@Component(modules = {MainActivityModule.class})
public interface MainActivityComponent {
    public void inject(MainActivity mainActivity);
}
