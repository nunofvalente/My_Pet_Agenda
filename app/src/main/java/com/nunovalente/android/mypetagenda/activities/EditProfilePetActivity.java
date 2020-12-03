package com.nunovalente.android.mypetagenda.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.ActivityEditProfilePetBinding;
import com.nunovalente.android.mypetagenda.fragments.MyPetsFragment;
import com.nunovalente.android.mypetagenda.fragments.PetProfileFragment;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditProfilePetActivity extends AppCompatActivity {

    private FirebaseViewModel firebaseViewModel;
    private ActivityEditProfilePetBinding mBinding;

    private final Calendar mCalendar = Calendar.getInstance();

    private Pet pet;
    private final List<String> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile_pet);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pet = (Pet) bundle.getSerializable(PetProfileFragment.PET);
        }
        mBinding.setPet(pet);
        setSpinner();

        EditProfilePetClickHandler mHandler = new EditProfilePetClickHandler();
        mBinding.setClickHandler(mHandler);
    }

    private void setSpinner() {
        String[] mAnimalTypes = getResources().getStringArray(R.array.animal_type);
        mBinding.spinnerEditPet.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mAnimalTypes));
        String type = pet.getType();
        ArrayAdapter myAdapt = (ArrayAdapter) mBinding.spinnerEditPet.getAdapter();
        int spinnerPosition = myAdapt.getPosition(type);
        mBinding.spinnerEditPet.setSelection(spinnerPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            try {
                Uri imageSelected = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageSelected);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                ImageView image = findViewById(R.id.image_add_pet);
                image.setImageBitmap(selectedImage);

                photoList.add(imageSelected.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

public class EditProfilePetClickHandler {

    public EditProfilePetClickHandler() {
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);
        }
    }

    public void chooseDate(View view) {

        DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        new DatePickerDialog(EditProfilePetActivity.this, date, mCalendar
                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateLabel() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.UK);

        String date = sdf.format(mCalendar.getTime());
        mBinding.editPetProfileBirthday.setText(date);
    }

    public void updatePetInfo(View view) {
        String name = mBinding.editNameProfilePet.getText().toString();
        String type = mBinding.spinnerEditPet.getSelectedItem().toString();
        String weight = mBinding.editPetProfileWeight.getText().toString();
        String birthday = mBinding.editPetProfileBirthday.getText().toString();
        String breed = mBinding.editPetProfileBreed.getText().toString();

        if (!birthday.isEmpty()) {
            if (!name.isEmpty()) {
                pet.setType(type);
                pet.setName(name);
                pet.setWeight(weight);
                pet.setBirthday(birthday);
                pet.setBreed(breed);

                if(!photoList.isEmpty()) {
                    firebaseViewModel.updatePetInfo(EditProfilePetActivity.this, FirebaseHelper.getUserId(), Constants.PET_PIC, pet.getName(), photoList.get(0), pet);
                } else {
                    firebaseViewModel.updatePetInfo(EditProfilePetActivity.this, FirebaseHelper.getUserId(), Constants.PET_PIC, pet.getName(), "", pet);
                }
                finish();
            } else {
                Toast.makeText(EditProfilePetActivity.this, "Please type the pet name!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditProfilePetActivity.this, "Please fill the pet birthday!", Toast.LENGTH_SHORT).show();
        }
    }
}
}
