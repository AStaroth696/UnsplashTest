package com.example.android.unsplashtest.service;

import com.example.android.unsplashtest.activity.MainActivity;
import com.example.android.unsplashtest.presenter.MainPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for MainPresenter
 */
@Module
public class MainActivityModule {
    MainActivity context;

    public MainActivityModule(MainActivity context) {
        this.context = context;
    }

    @Provides
    public MainPresenter provideMainPresenter(){
        return new MainPresenter(context);
    }
}
