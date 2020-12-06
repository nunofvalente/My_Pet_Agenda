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
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.ActivityAddPetBinding;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.Permission;
import com.nunovalente.android.mypetagenda.util.StringGenerator;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddPetActivity extends AppCompatActivity {

    private static final String PET = "pet";

    private FirebaseViewModel firebaseViewModel;
    private ActivityAddPetBinding mBinding;
    private RoomViewModel roomViewModel;

    private final List<String> photoList = new ArrayList<>();

    private final Pet pet = new Pet("", "", "", "", "", "", "");
    private final Calendar mCalendar = Calendar.getInstance();

    private final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_pet);
        configureToolbar();
        Permission.validatePermissions(new ArrayList<>(Arrays.asList(permissions)), this, 1);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        roomViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(RoomViewModel.class);

        setSpinner();

        AddPetActivityClickHandler mHandler = new AddPetActivityClickHandler();
        mBinding.setClickHandler(mHandler);


        mBinding.setPet(pet);

    }

    private void setSpinner() {
        String[] mAnimalTypes = getResources().getStringArray(R.array.animal_type);
        mBinding.spinnerMyPets.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mAnimalTypes));
    }

    private void configureToolbar() {
        setSupportActionBar(mBinding.toolbarPets);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            mBinding.toolbarPetsTitle.setText(R.string.add_pet);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.lobster_regular);
            mBinding.toolbarPetsTitle.setTypeface(typeface);
        }
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
            assert data != null;
            Uri imageSelected = data.getData();
            Glide.with(this).load(imageSelected).into(mBinding.imageAddPet);


            photoList.add(imageSelected.toString());

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String name = mBinding.editNamePet.getText().toString();
        String type = mBinding.spinnerMyPets.getSelectedItem().toString();
        String weight = mBinding.editPetWeight.getText().toString();
        String birthday = mBinding.editPetBirthday.getText().toString();
        String breed = mBinding.editPetBreed.getText().toString();

        pet.setName(name);
        pet.setType(type);
        pet.setWeight(weight);
        pet.setBirthday(birthday);
        pet.setBreed(breed);

        outState.putSerializable(PET, pet);

    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        if (savedInstanceState != null) {
            Pet pet = (Pet) savedInstanceState.getSerializable(PET);
            mBinding.setPet(pet);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public class AddPetActivityClickHandler {

        public AddPetActivityClickHandler() {
        }

        public void chooseDate(View view) {

            DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            };

            new DatePickerDialog(AddPetActivity.this, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)).show();

        }

        private void updateLabel() {
            String format = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.UK);

            String date = sdf.format(mCalendar.getTime());
            mBinding.editPetBirthday.setText(date);
            pet.setBirthday(date);
        }

        public void savePet(View view) {
            String name = mBinding.editNamePet.getText().toString();
            String type = mBinding.spinnerMyPets.getSelectedItem().toString();
            String weight = mBinding.editPetWeight.getText().toString();
            String birthday = mBinding.editPetBirthday.getText().toString();
            String breed = mBinding.editPetBreed.getText().toString();

            if (!birthday.isEmpty()) {
                if (!name.isEmpty()) {
                    if(photoList.size() != 0) {
                        pet.setType(type);
                        pet.setName(name);
                        pet.setWeight(weight);
                        pet.setBirthday(birthday);
                        pet.setBreed(breed);
                        pet.setId(StringGenerator.getRandomString());

                        firebaseViewModel.storePetImage(AddPetActivity.this, FirebaseHelper.getUserId(), Constants.PET_PIC, pet.getName(), photoList.get(0), pet, roomViewModel);
                        finish();
                    } else {
                        Toast.makeText(AddPetActivity.this, getString(R.string.please_upload_a_picture), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddPetActivity.this, "Please type the pet name!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddPetActivity.this, "Please fill the pet birthday!", Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint("QueryPermissionsNeeded")
        public void openGallery(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);
            }
        }
    }
}