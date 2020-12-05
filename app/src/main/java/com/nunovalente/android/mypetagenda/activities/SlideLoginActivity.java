package com.nunovalente.android.mypetagenda.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.dialog.DialogResetPassword;
import com.nunovalente.android.mypetagenda.databinding.ActivityLoginBinding;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

public class SlideLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = SlideLoginActivity.class.getSimpleName();

    private ActivityLoginBinding mBinding;
    private FirebaseViewModel firebaseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);

        setListeners();
    }

    private void setListeners() {
        mBinding.buttonLoginSignIn.setOnClickListener(this);
        mBinding.loginForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_login_sign_in) {
            signIn();
        } else if (v.getId() == R.id.login_forgot_password) {
            openDialogResetPassword();
        }
    }

    private void openDialogResetPassword() {
        DialogResetPassword dialog = new DialogResetPassword();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), TAG);
    }

    private void signIn() {
        ProgressBar progressBar = findViewById(R.id.progress_login_activity);
        String email = mBinding.editLoginEmail.getText().toString();
        String password = mBinding.editLoginPassword.getText().toString();

        firebaseViewModel.signInOwner(email, password, this, progressBar);
    }
}