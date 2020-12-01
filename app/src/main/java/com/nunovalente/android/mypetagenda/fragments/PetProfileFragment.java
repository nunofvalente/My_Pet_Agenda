package com.nunovalente.android.mypetagenda.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.AddReminderActivity;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetProfileBinding;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class PetProfileFragment extends Fragment {

    private FragmentPetProfileBinding mBinding;

    private FragmentShareViewModel fragmentShareViewModel;

    public static final String PET = "pet";
    private Pet mPet = null;

    public PetProfileFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentShareViewModel = new ViewModelProvider(requireActivity()).get(FragmentShareViewModel.class);
        fragmentShareViewModel.getSelectedPet().observe(getViewLifecycleOwner(), pet -> {
            mBinding.setPet(pet);
            mPet = pet;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_profile, container, false);
        View root = mBinding.getRoot();

      /*  Bundle bundle = this.getArguments();
        if (bundle != null) {
            pet = (Pet) bundle.getSerializable(PET);
            mBinding.setPet(pet);
        }*/

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getParentFragmentManager(), FragmentPagerItems.with(getContext())
                .add(R.string.reminders, PetRemindersFragment.class)
                .add(R.string.activities, PetNotesFragment.class)
                .add(R.string.edit_profile, PetInformationFragment.class)
                .create());

        PetProfileFragmentClickHandler mHandler = new PetProfileFragmentClickHandler();
        mBinding.setClickHandler(mHandler);

        mBinding.pagerPetProfile.setAdapter(adapter);
        mBinding.smartTabPetProfile.setViewPager(mBinding.pagerPetProfile);

        return root;
    }

    public class PetProfileFragmentClickHandler {

        public PetProfileFragmentClickHandler() {
        }

        public void addReminder(View view) {
            Intent intent = new Intent(getContext(), AddReminderActivity.class);
            intent.putExtra(PET, mPet);
            startActivity(intent);
            getActivity().finish();
        }

    }
}