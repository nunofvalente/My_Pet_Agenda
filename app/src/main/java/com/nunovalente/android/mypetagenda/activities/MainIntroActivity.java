package com.nunovalente.android.mypetagenda.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.dialog.DialogRegister;
import com.nunovalente.android.mypetagenda.activities.dialog.DialogRegisterJoinAccount;

public class MainIntroActivity extends IntroActivity {

    private static final String TAG = MainIntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        addSlides();
    }

    private void addSlides() {

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.fragment_slide_1)
                .canGoBackward(false)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.fragment_slide_2)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.fragment_slide_3)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.fragment_slide_4)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.fragment_slide_5)
                .canGoForward(false)
                .build());

    }

    public void openDialogRegister(View view) {
        DialogRegister dialogRegister = new DialogRegister();
        dialogRegister.setCancelable(false);
        dialogRegister.show(getSupportFragmentManager(), TAG);
    }

    public void openDialogJoinAccount(View view) {
        DialogRegisterJoinAccount dialogRegisterJoinAccount = new DialogRegisterJoinAccount();
        dialogRegisterJoinAccount.setCancelable(false);
        dialogRegisterJoinAccount.show(getSupportFragmentManager(), TAG);
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(this, SlideLoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean(getString(R.string.pref_previously_started), false)) {
            moveToSecondary();
        }
    }

    private void moveToSecondary() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
