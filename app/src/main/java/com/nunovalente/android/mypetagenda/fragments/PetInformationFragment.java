package com.nunovalente.android.mypetagenda.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetInformationBinding;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;

import java.util.Calendar;

public class PetInformationFragment extends Fragment {

    private FragmentPetInformationBinding mBinding;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    public PetInformationFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_information, container, false);
        View root = mBinding.getRoot();

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(getActivity()).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();

        retrievePetInformation();

        return root;
    }

    private void retrievePetInformation() {
        valueEventListener = databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mBinding.progressPetInformation.setVisibility(View.VISIBLE);
                Owner owner = snapshot.getValue(Owner.class);
                getPet(owner.getAccountId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
                mBinding.progressPetInformation.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getPet(String accountId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String petId = prefs.getString(getString(R.string.selected_pet_id), "");
        databaseReference.child(Constants.PETS).child(accountId).child(petId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pet pet = snapshot.getValue(Pet.class);
                mBinding.setPet(pet);
                mBinding.progressPetInformation.setVisibility(View.INVISIBLE);
                int age = getAge(pet.getBirthday());
                mBinding.tvPetAge.setText(String.valueOf(age));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
                mBinding.progressPetInformation.setVisibility(View.INVISIBLE);
            }
        });
    }

    private int getAge(String birthday) {
        String yearBorn = birthday.substring(6);
        int yearInt = Integer.parseInt(yearBorn);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        return currentYear-yearInt;
    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }
}