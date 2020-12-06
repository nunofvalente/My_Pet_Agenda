package com.nunovalente.android.mypetagenda.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.adapters.RecyclerAddReminderAdapter;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetRemindersBinding;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PetRemindersFragment extends Fragment {

    private FragmentPetRemindersBinding mBinding;
    private RoomViewModel roomViewModel;

    private List<Reminder> mReminderList = new ArrayList<>();

    public PetRemindersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentShareViewModel fragmentShareViewModel = new ViewModelProvider(requireActivity()).get(FragmentShareViewModel.class);
        fragmentShareViewModel.getSelectedPet().observe(getViewLifecycleOwner(), this::retrieveReminders);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_reminders, container, false);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);


        return mBinding.getRoot();
    }

    private void retrieveReminders(Pet pet) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");
        roomViewModel.getAllReminders(accountId).observe(this, reminders -> {
            mReminderList = reminders;
            setRecyclerView(mReminderList);
        });
    }

    private void setRecyclerView(List<Reminder> reminderList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerReminders.setLayoutManager(linearLayoutManager);
        mBinding.recyclerReminders.setHasFixedSize(true);
        RecyclerAddReminderAdapter adapter = new RecyclerAddReminderAdapter(reminderList);
        mBinding.recyclerReminders.setAdapter(adapter);
    }
}