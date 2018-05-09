package com.example.android.unsplashtest.service;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.unsplashtest.R;
import com.example.android.unsplashtest.presenter.MainPresenter;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{
    private MainPresenter presenter;
    //private List<Photo> photos;

    public PhotoAdapter(MainPresenter presenter) {
        Log.d("ADAPTER", "created");
        this.presenter = presenter;
        //photos = presenter.getPhotoList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        presenter.bindView(holder.photo, position);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.itemClicked(presenter.getPhotoList().get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return presenter.getPhotoList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View container;
        ImageView photo;
        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView;
            photo = itemView.findViewById(R.id.photo);
        }
    }
}