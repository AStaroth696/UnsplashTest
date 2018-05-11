package com.example.android.unsplashtest.networking;

import com.example.android.unsplashtest.presenter.MainPresenter;

import dagger.Component;

/**
 * Dagger component for Retrofit service
 */
@Component(modules = {NetworkModule.class})
public interface NetworkComponent {
    public void inject(MainPresenter presenter);
}
