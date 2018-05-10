package com.example.android.unsplashtest.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.android.unsplashtest.activity.DetailActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DetailPresenter{
    private DetailActivity context;

    public DetailPresenter(DetailActivity detailActivity) {
        this.context = detailActivity;
    }

    public void loadPhotoIntoContainer(PhotoView photo, String url){
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

    public void downloadPhoto(String url, final String id, WebView webView){
        context.setProgressBarVisible();
        context.showToast("Downloading...");
        Picasso.with(context).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("DOWNLOADING", "downloading file");
                File file = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/" + id + ".jpeg");
                Log.d("DOWNLOADING", Environment.getExternalStorageDirectory().getPath()
                        + "/" + id);
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    context.setProgressBarInvisible();
                    Log.d("DOWNLOADING", "image downloaded");
                    context.showToast("Photo downloaded");
                } catch (IOException e) {
                    e.printStackTrace();
                    context.setProgressBarInvisible();
                    context.showToast("Download error");
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("DOWNLOADING", "downloading failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
//        webView.loadUrl(url);
//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d("DOWNLOADING", "url");
//                if (url.contains("images.unsplash.com")){
//                    Picasso.with(context).load(url).into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            Log.d("DOWNLOADING", "downloading file");
//                            File file = new File(Environment.getExternalStorageDirectory().getPath()
//                                    + "/" + id);
//                            try {
//                                file.createNewFile();
//                                FileOutputStream fos = new FileOutputStream(file);
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                                fos.close();
//                                context.setProgressBarInvisible();
//                                context.showToast("Photo downloaded");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable errorDrawable) {
//                            Log.d("DOWNLOADING", "downloading failed");
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                        }
//                    });
//
//                }
//                view.loadUrl(url);
//                return false;
//            }
//        });
    }
}
