package com.example.android.unsplashtest.service;

import com.example.android.unsplashtest.activity.MainActivity;

import dagger.Component;

/**
 * Dagger component for MainPresenter
 */
@Component(modules = {MainActivityModule.class})
public interface MainActivityComponent {
    public void inject(MainActivity mainActivity);
}
