package com.nunovalente.android.mypetagenda.adapters;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerGalleryAdapter extends RecyclerView.Adapter<RecyclerGalleryAdapter.MyGalleryViewHolder> {

    private List<Bitmap> mImageList;

    @NonNull
    @Override
    public MyGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyGalleryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class MyGalleryViewHolder extends RecyclerView.ViewHolder {

        public MyGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
