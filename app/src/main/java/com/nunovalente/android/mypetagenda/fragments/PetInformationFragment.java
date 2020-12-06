package com.nunovalente.android.mypetagenda.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetInformationBinding;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class PetInformationFragment extends Fragment {

    private FragmentPetInformationBinding mBinding;

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
            if(pet.getBreed().equals("")) {
                mBinding.editInformationBreed.setText("N/A");
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_information, container, false);

        return mBinding.getRoot();
    }

    private int getAge(String birthday) {
        String yearBorn = birthday.substring(6);
        int yearInt = Integer.parseInt(yearBorn);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        return currentYear-yearInt;
    }
}