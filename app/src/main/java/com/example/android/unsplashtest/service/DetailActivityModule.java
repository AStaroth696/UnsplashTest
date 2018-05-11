package com.example.android.unsplashtest.service;

import com.example.android.unsplashtest.activity.DetailActivity;
import com.example.android.unsplashtest.presenter.DetailPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for DetailPresenter
 */
@Module
public class DetailActivityModule {
    DetailActivity context;

    public DetailActivityModule(DetailActivity context) {
        this.context = context;
    }

    @Provides
    public DetailPresenter provideDetailPresenter(){
        return new DetailPresenter(context);
    }
}
