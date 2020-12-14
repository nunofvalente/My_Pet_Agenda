package com.nunovalente.android.mypetagenda.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nunovalente.android.mypetagenda.R;

import java.util.List;

public class RecyclerGalleryAdapter extends RecyclerView.Adapter<RecyclerGalleryAdapter.MyGalleryViewHolder> {

    private final List<Bitmap> mImageList;
    private final Context context;
    private final RecyclerItemClickListener listener;

    public RecyclerGalleryAdapter(Context context, List<Bitmap> mImageList, RecyclerItemClickListener listener) {
        this.context = context;
        this.mImageList = mImageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_adapter, parent, false);

        return new MyGalleryViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MyGalleryViewHolder holder, int position) {
        Bitmap bitmap = mImageList.get(position);
        if(bitmap != null) {
            Glide.with(context).load(bitmap).placeholder(R.drawable.default_image).into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public class MyGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageView;

        public MyGalleryViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_gallery);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            listener.onItemClickListener(position);
        }
    }
}
