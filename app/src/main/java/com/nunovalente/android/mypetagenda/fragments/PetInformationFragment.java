package com.nunovalente.android.mypetagenda.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.util.Calendar;

public class PetInformationFragment extends Fragment {

    private FragmentPetInformationBinding mBinding;

    private RoomViewModel roomViewModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    public PetInformationFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentShareViewModel fragmentShareViewModel = new ViewModelProvider(requireActivity()).get(FragmentShareViewModel.class);
        fragmentShareViewModel.getSelectedPet().observe(getViewLifecycleOwner(), pet -> {
            mBinding.setPet(pet);
            int age = getAge(pet.getBirthday());
            mBinding.tvPetAge.setText(String.valueOf(age));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_information, container, false);
        View root = mBinding.getRoot();

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(getActivity()).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);


        return root;
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
        if(valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}