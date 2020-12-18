package com.nunovalente.android.mypetagenda.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.adapters.RecyclerHomeAdapter;
import com.nunovalente.android.mypetagenda.databinding.FragmentHomeBinding;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RoomViewModel roomViewModel;
    private DatabaseReference database;

    private FragmentHomeBinding mBinding;

    private final List<String> mKeyList = new ArrayList<>();

    private String accountId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        FirebaseViewModel firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        database = firebaseViewModel.getDatabase();

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View root = mBinding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");

        loadReminders();
        performSyncPets();


        this.setExitTransition(new MaterialFadeThrough().setDuration(getResources().getInteger(R.integer.reply_motion_duration_large)));
        return root;
    }

    private void performSyncPets() {
        database.child(Constants.PETS).child(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasChildren = snapshot.hasChildren();
                if (hasChildren) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String key = data.getKey();
                        mKeyList.add(key);
                    }
                    savePetToStorage(mKeyList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Sync", error.getMessage());
            }
        });
    }

    private void savePetToStorage(List<String> list) {
        for (String key : list) {
            database.child(Constants.PETS).child(accountId).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Pet pet = snapshot.getValue(Pet.class);
                    roomViewModel.insertPet(pet);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void loadReminders() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");

        roomViewModel.getAllReminders(accountId).observe(requireActivity(), this::setRecyclerView);
    }

    private void setRecyclerView(List<Reminder> reminders) {
        if(reminders.isEmpty()) {
            mBinding.tvRemindersEmpty.setVisibility(View.VISIBLE);
        } else {
            mBinding.tvRemindersEmpty.setVisibility(View.INVISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            RecyclerHomeAdapter adapter = new RecyclerHomeAdapter(reminders);
            mBinding.recyclerHome.setLayoutManager(linearLayoutManager);
            mBinding.recyclerHome.setAdapter(adapter);
            mBinding.recyclerHome.setHasFixedSize(true);
        }
    }
}