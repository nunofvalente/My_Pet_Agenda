package com.nunovalente.android.mypetagenda.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.WindowManager;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;

import static com.nunovalente.android.mypetagenda.fragments.PetProfileFragment.PET;

public class PetProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pet_profile);

        FragmentShareViewModel fragmentShareViewModel = new ViewModelProvider(this).get(FragmentShareViewModel.class);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fragmentShareViewModel.selectPet((Pet) bundle.getSerializable(PET));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}