package com.nunovalente.android.mypetagenda.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Transition;
import android.view.View;

import com.google.android.material.transition.platform.MaterialFadeThrough;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.ActivityProfileBinding;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.util.Permission;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings( "deprecation" )
public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding mBinding;
    private FirebaseViewModel firebaseViewModel;
    private RoomViewModel roomViewModel;

    private String userId;

    private final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        configureToolbar();

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        Permission.validatePermissions(new ArrayList<>(Arrays.asList(permissions)), this, 1);

        ProfileActivityClickHandler mHandler = new ProfileActivityClickHandler();
        mBinding.setClickHandler(mHandler);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        userId = sharedPreferences.getString(getString(R.string.pref_user_id), "");

        if(NetworkUtils.checkConnectivity(getApplication()) && FirebaseHelper.getCurrentOwner() != null) {
            loadUserData();
        } else {
            loadOfflineUserData();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadOfflineUserData() {
        AsyncTask<String , Void, Owner> task = new AsyncTask<String, Void, Owner>() {
            @Override
            protected Owner doInBackground(String... strings) {
                return roomViewModel.getOwner(strings[0]);
            }

            @Override
            protected void onPostExecute(Owner owner) {
                super.onPostExecute(owner);
                mBinding.setOwner(owner);
                mBinding.editProfileName.setFocusable(false);
            }
        };

        task.execute(userId);
    }

    private void loadUserData() {
        Owner owner = FirebaseHelper.getLoggedUserData();
        mBinding.setOwner(owner);
    }

    private void configureToolbar() {
        setSupportActionBar(mBinding.toolbarEditProfile);
        getSupportActionBar().setTitle("");
        Typeface typeface = ResourcesCompat.getFont(this, R.font.lobster_regular);
        mBinding.toolbarTitle.setTypeface(typeface);
        mBinding.toolbarTitle.setText(getString(R.string.edit_profile));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult : grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                alertValidatePermission();
            }
        }
    }

    private void alertValidatePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions Denied");
        builder.setMessage("Permissions are required to use the app.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", (dialog, which) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap image = null;

            try {
                if (requestCode == Constants.REQUEST_CODE_GALLERY) {
                    assert data != null;
                    Uri imagePath = data.getData();
                    if (Build.VERSION.SDK_INT < 28) {
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    } else {
                        image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imagePath));
                    }
                }

                if (image != null) {

                    mBinding.circleImageProfile.setImageBitmap(image);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    firebaseViewModel.storeImage(this, FirebaseHelper.getUserId(), Constants.PROFILE_PIC, Constants.PROFILE_PIC_NAME, imageData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
        return false;
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }

    public class ProfileActivityClickHandler {

        public ProfileActivityClickHandler() {
        }

        @SuppressLint("QueryPermissionsNeeded")
        public void openGallery(View view) {
            if(NetworkUtils.checkConnectivity(getApplication()) && FirebaseHelper.getCurrentOwner() != null) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);
                }
            } else {
                mBinding.tvNetworkErrorAccount.setVisibility(View.VISIBLE);
            }
        }

        public void updateProfile(View view) {
            if(NetworkUtils.checkConnectivity(getApplication()) && FirebaseHelper.getCurrentOwner() != null) {
                String name = mBinding.editProfileName.getText().toString();
                boolean displayName = FirebaseHelper.updateOwnerName(name);

                if (displayName) {
                    Owner owner = FirebaseHelper.getLoggedUserData();
                    owner.setName(name);
                    owner.updateUser();
                    mBinding.notifyChange();

                    //SQL
                    roomViewModel.updateOwner(name, userId);
                }
                finish();
            } else {
                if(!NetworkUtils.checkConnectivity(getApplication())) {
                    mBinding.tvNetworkErrorAccount.setVisibility(View.VISIBLE);
                } else {
                    mBinding.tvNetworkErrorAccount.setVisibility(View.VISIBLE);
                    mBinding.tvNetworkErrorAccount.setText(R.string.plrease_sign_in_to_make_changes);
                }
            }
        }
    }
}