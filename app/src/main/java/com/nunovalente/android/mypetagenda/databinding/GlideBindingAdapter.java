package com.nunovalente.android.mypetagenda.databinding;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nunovalente.android.mypetagenda.R;

public class GlideBindingAdapter {

    @BindingAdapter("imageUrl")
    public static void setImageResource(ImageView view, String imageUrl) {
        Context context = view.getContext();

        RequestOptions options = new RequestOptions()
                .dontAnimate()
                .placeholder(R.drawable.default_account)
                .error(R.drawable.default_account);

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(imageUrl)
                .into(view);
    }

    @BindingAdapter("imageUrlPet")
    public static void setImageResourcePet(ImageView view, String imageUrl) {
        Context context = view.getContext();

        RequestOptions options = new RequestOptions()
                .dontAnimate()
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image);

        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(imageUrl)
                .into(view);
    }
}
