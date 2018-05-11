package com.example.android.unsplashtest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.unsplashtest.R;
import com.example.android.unsplashtest.presenter.DetailPresenter;
import com.example.android.unsplashtest.service.DaggerDetailActivityComponent;
import com.example.android.unsplashtest.service.DetailActivityModule;
import com.github.chrisbanes.photoview.PhotoView;

import javax.inject.Inject;

/**
 * Activity that represents selected photo
 */
public class DetailActivity extends AppCompatActivity {
    public static final String PHOTO_ID = "photo id";
    public static final String PHOTO_URL = "photo url";
    @Inject
    DetailPresenter presenter;
    private String photoId;
    private String photoUrl;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        DaggerDetailActivityComponent.builder().detailActivityModule(new DetailActivityModule(this))
                .build().inject(this);
        if (getIntent().getExtras() != null) {
            photoId = getIntent().getStringExtra(PHOTO_ID);
            photoUrl = getIntent().getStringExtra(PHOTO_URL);
        }
        progressBar = findViewById(R.id.download_progress_bar);
        PhotoView largePhoto = findViewById(R.id.large_photo);
        presenter.loadPhotoIntoContainer(largePhoto, photoUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_download:
                presenter.downloadPhoto(photoUrl, photoId);
                break;
            case R.id.action_set_wallpaper:
                presenter.setWallpaper(photoUrl);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void setProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setProgressBarInvisible() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
