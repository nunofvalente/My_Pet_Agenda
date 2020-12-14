package com.nunovalente.android.mypetagenda.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.ActivityPreviewBinding;

import java.io.FileInputStream;

public class PreviewActivity extends AppCompatActivity {

    private ActivityPreviewBinding mBinding;

    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_preview);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String filename = getIntent().getStringExtra(getString(R.string.selected_photo));
            try {
                FileInputStream fis = this.openFileInput(filename);
                bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        loadImage();
    }

    private void loadImage() {
        if (bitmap != null) {
            Glide.with(this).load(bitmap).placeholder(R.drawable.default_account).into(mBinding.imagePreview);
        }
    }
}