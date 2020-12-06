package com.nunovalente.android.mypetagenda.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.dialog.DialogResetPassword;
import com.nunovalente.android.mypetagenda.databinding.ActivityLoginMainBinding;
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginMainBinding mBinding;
    private FirebaseViewModel firebaseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_main);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);

        setToolbar();

        mBinding.buttonLoginMainSignIn.setOnClickListener(v -> {
            String email = mBinding.editLoginMainEmail.getText().toString();
            String password = mBinding.editLoginMainPassword.getText().toString();

            if(NetworkUtils.checkConnectivity(getApplication())) {
                mBinding.tvNetworkErrorMain.setVisibility(View.INVISIBLE);
                firebaseViewModel.signInOwner(email, password, LoginActivity.this, mBinding.progressLoginMain);
            } else {
                mBinding.tvNetworkErrorMain.setVisibility(View.VISIBLE);
            }
        });

        mBinding.loginMainForgotPassword.setOnClickListener(v -> {
                DialogResetPassword dialog = new DialogResetPassword();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), TAG);
        });
    }

    private void setToolbar() {
        setSupportActionBar(mBinding.toolbarLoginMain);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        Typeface typeface = ResourcesCompat.getFont(this, R.font.lobster_regular);
        mBinding.toolbarLoginMainTitle.setTypeface(typeface);
        mBinding.toolbarLoginMainTitle.setText(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}