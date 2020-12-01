package com.nunovalente.android.mypetagenda.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.adapters.RecyclerAddReminderAdapter;
import com.nunovalente.android.mypetagenda.adapters.RecyclerPetAdapter;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetRemindersBinding;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;

import java.util.ArrayList;
import java.util.List;

public class PetRemindersFragment extends Fragment {

    private FragmentPetRemindersBinding mBinding;

    private FirebaseViewModel firebaseViewModel;
    private DatabaseReference databaseReference;
    private FragmentShareViewModel fragmentShareViewModel;
    private ValueEventListener valueEventListener;

    private final List<Reminder> mReminderList = new ArrayList<>();

    public PetRemindersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentShareViewModel = new ViewModelProvider(requireActivity()).get(FragmentShareViewModel.class);
        fragmentShareViewModel.getSelectedPet().observe(getViewLifecycleOwner(), this::retrieveReminders);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_reminders, container, false);

        firebaseViewModel = new ViewModelProvider(getActivity()).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();

        return mBinding.getRoot();
    }

    private void retrieveReminders(Pet pet) {
        DatabaseReference databaseReference = firebaseViewModel.getDatabase();
        databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
               loadRecyclerView(owner.getAccountId(), pet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    private void loadRecyclerView(String accountId, Pet pet) {
        valueEventListener = databaseReference.child(Constants.REMINDERS).child(accountId).child(pet.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mReminderList.clear();

                for(DataSnapshot data: snapshot.getChildren()) {
                    Reminder reminder = data.getValue(Reminder.class);

                    mReminderList.add(reminder);
                }

                setRecyclerView(mReminderList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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