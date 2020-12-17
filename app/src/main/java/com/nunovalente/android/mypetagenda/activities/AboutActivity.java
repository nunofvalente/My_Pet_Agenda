package com.nunovalente.android.mypetagenda.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);
        configureToolbar();

    }

    private void configureToolbar() {
        setSupportActionBar(mBinding.toolbarAbout);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        Typeface typeface = ResourcesCompat.getFont(this, R.font.lobster_regular);
        mBinding.toolbarTitleAbout.setTypeface(typeface);
        mBinding.toolbarTitleAbout.setText(getString(R.string.about_us));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
        return true;
    }
}