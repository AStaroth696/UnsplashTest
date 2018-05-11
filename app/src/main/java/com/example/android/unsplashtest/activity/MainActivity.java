package com.example.android.unsplashtest.activity;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.android.unsplashtest.R;
import com.example.android.unsplashtest.model.Photo;
import com.example.android.unsplashtest.presenter.MainPresenter;
import com.example.android.unsplashtest.service.DaggerMainActivityComponent;
import com.example.android.unsplashtest.service.MainActivityModule;
import com.example.android.unsplashtest.service.PhotoAdapter;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    @Inject
    MainPresenter presenter;
    private RecyclerView recyclerView;
    PhotoAdapter adapter;
    private WebView webView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerMainActivityComponent.builder().mainActivityModule(new MainActivityModule(this))
                .build().inject(this);
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);
        if (savedInstanceState != null){
            presenter.onRestoreState(savedInstanceState);
        }else {
            presenter.processWebView(webView);
        }
        recyclerView = findViewById(R.id.photo_recycler);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        adapter = new PhotoAdapter(presenter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("SCROLLING", presenter.getCallbacks().size() + " : " + presenter.getPhotoList().size());
                if (presenter.isLoaded()
                        && layoutManager.getItemCount() >= 10
                        && layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                    presenter.receivePhotos();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.clearPhotoList();
                presenter.setSearching(true);
                presenter.setQuery(query);
                Log.d("PHOTOS", "clear photos. starting search");
                presenter.receivePhotos();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                if (presenter.isSearching()) {
                    presenter.setSearching(false);
                    presenter.clearPhotoList();
                    Log.d("PHOTOS", "clear photos. closing searchview");
                    presenter.receivePhotos();
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }

    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void setProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setProgressBarInvisible() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
