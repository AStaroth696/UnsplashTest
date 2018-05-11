package com.example.android.unsplashtest.presenter;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.example.android.unsplashtest.R;
import com.example.android.unsplashtest.activity.DetailActivity;

import com.example.android.unsplashtest.networking.NetworkStateInfo;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Presenter class for detail activity
 */
public class DetailPresenter{
    private DetailActivity context;

    public DetailPresenter(DetailActivity detailActivity) {
        this.context = detailActivity;
    }

    //Load photo into imageView by url
    public void loadPhotoIntoContainer(PhotoView photo, String url){
        if (NetworkStateInfo.isNetworkAvailable(context)) {
            context.setProgressBarVisible();
            Picasso.with(context).load(url).into(photo, new Callback() {
                @Override
                public void onSuccess() {
                    context.setProgressBarInvisible();
                }

                @Override
                public void onError() {
                    context.setProgressBarInvisible();
                }
            });
        }
    }

    //Download photo into root folder
    public void downloadPhoto(String url, final String id) {
        if (NetworkStateInfo.isNetworkAvailable(context)) {
            context.setProgressBarVisible();
            context.showToast(context.getResources().getString(R.string.downloading));
            Picasso.with(context).load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    File file = new File(Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                            + "/" + id + ".jpeg");
                    try {
                            file.createNewFile();
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            context.setProgressBarInvisible();
                            context.showToast(context.getResources().getString(R.string.photo_downloaded));
                    } catch (IOException e) {
                        e.printStackTrace();
                        context.setProgressBarInvisible();
                        context.showToast(context.getResources().getString(R.string.download_error));
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
        }
    }

    //Set photo as wallpaper
    public void setWallpaper(String url){
        Picasso.with(context).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager wpmanager = WallpaperManager.getInstance(context);
                try {
                    wpmanager.setBitmap(bitmap);
                    context.showToast(context.getResources().getString(R.string.set_done));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}
