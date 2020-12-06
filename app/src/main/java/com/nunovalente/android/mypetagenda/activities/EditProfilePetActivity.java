package com.nunovalente.android.mypetagenda.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.ActivityEditProfilePetBinding;
import com.nunovalente.android.mypetagenda.fragments.PetProfileFragment;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditProfilePetActivity extends AppCompatActivity {

    private FirebaseViewModel firebaseViewModel;
    private RoomViewModel roomViewModel;
    private ActivityEditProfilePetBinding mBinding;
    private Uri imageSelected;

    private final Calendar mCalendar = Calendar.getInstance();

    private Pet pet;
    private final List<String> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile_pet);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

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
            Bitmap image = null;

            try {
                if (requestCode == Constants.REQUEST_CODE_GALLERY) {
                    assert data != null;
                    imageSelected = data.getData();
                    if (Build.VERSION.SDK_INT < 28) {
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageSelected);
                    } else {
                        image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageSelected));
                    }
                }

                if (image != null) {
                    mBinding.imageEditPet.setImageBitmap(image);
                }
                photoList.add(imageSelected.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class EditProfilePetClickHandler {

        public EditProfilePetClickHandler() {
        }

        public void openGallery(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Constants.REQUEST_CODE_GALLERY);
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

                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                    String accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");

                    Intent intent = new Intent(EditProfilePetActivity.this, PetProfileActivity.class);

                    if (!photoList.isEmpty()) {
                        firebaseViewModel.updatePetInfo(EditProfilePetActivity.this, FirebaseHelper.getUserId(), Constants.PET_PIC, pet.getName(), photoList.get(0), pet);
                        pet.setImagePath(imageSelected.toString());

                    } else {
                        firebaseViewModel.updatePetInfo(EditProfilePetActivity.this, FirebaseHelper.getUserId(), Constants.PET_PIC, pet.getName(), "", pet);
                    }

                    intent.putExtra(getString(R.string.PET), pet);
                    pet.setAccountId(accountId);
                    roomViewModel.updatePet(pet);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Toast.makeText(EditProfilePetActivity.this, "Please type the pet name!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditProfilePetActivity.this, "Please fill the pet birthday!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
