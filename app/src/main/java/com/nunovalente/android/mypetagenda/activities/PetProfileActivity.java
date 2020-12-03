package com.nunovalente.android.mypetagenda.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.fragments.MyPetsFragment;
import com.nunovalente.android.mypetagenda.fragments.PetProfileFragment;
import com.nunovalente.android.mypetagenda.fragments.PetRemindersFragment;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;

import static com.nunovalente.android.mypetagenda.fragments.PetProfileFragment.PET;

@SuppressWarnings( "deprecation" )
public class PetProfileActivity extends AppCompatActivity {

    private Pet pet;

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
            pet = (Pet) bundle.getSerializable(PET);
        }
    }
}