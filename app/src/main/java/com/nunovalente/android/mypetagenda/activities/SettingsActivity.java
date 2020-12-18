package com.nunovalente.android.mypetagenda.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.ActivitySettingsBinding;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.util.Base64Custom;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

public class SettingsActivity extends AppCompatActivity {

    private final static String TAG = SettingsActivity.class.getSimpleName();

    private ActivitySettingsBinding mBinding;

    private RoomViewModel roomViewModel;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        configureToolbar();

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        setListeners();
    }

    private void setListeners() {
        if (NetworkUtils.checkConnectivity(getApplication()) && FirebaseHelper.getCurrentOwner() != null) {
            mBinding.tvDeleteAccount.setOnClickListener(v -> showDialog());
        }
    }

    private void showDialog() {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.delete_account);
            alert.setMessage(R.string.delete_account_confirmation);
            alert.setPositiveButton(R.string.confirm, (dialog, which) -> {
                deleteUser();
                dialog.dismiss();
            });
            alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
            alert.show();
        }

    private void deleteUser() {
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean(getString(R.string.pref_previously_started), false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(getString(R.string.pref_previously_started), Boolean.FALSE);
            editor.apply();
        }

        FirebaseUser owner = FirebaseHelper.getCurrentOwner();
        String accountId = sharedpreferences.getString(getString(R.string.pref_account_id), "");

        if (owner.getEmail() != null) {
            databaseReference.child(Constants.ACCOUNT).child(accountId).child(Base64Custom.encodeString(owner.getEmail())).removeValue();

            DatabaseReference pathUser = databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId());
            pathUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Owner owner = snapshot.getValue(Owner.class);
                    roomViewModel.deleteOwner(owner);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "Account deleted");
                }
            });

            owner.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("TAG", "Account deleted");
                }
            });
        }

        Intent intent = new Intent(this, MainIntroActivity.class);
        startActivity(intent);
        finish();
    }

    private void configureToolbar() {
        setSupportActionBar(mBinding.toolbarSettings);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        Typeface typeface = ResourcesCompat.getFont(this, R.font.lobster_regular);
        mBinding.toolbarTitleSettings.setTypeface(typeface);
        mBinding.toolbarTitleSettings.setText(getString(R.string.settings));
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