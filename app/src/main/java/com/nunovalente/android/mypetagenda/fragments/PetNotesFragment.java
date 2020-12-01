package com.nunovalente.android.mypetagenda.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.adapters.RecyclerNoteAdapter;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetNotesBinding;
import com.nunovalente.android.mypetagenda.model.Note;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;

import java.util.ArrayList;
import java.util.List;

public class PetNotesFragment extends Fragment {

    private FirebaseViewModel firebaseViewModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private FragmentPetNotesBinding mBinding;

    private final List<Note> mNoteList = new ArrayList<>();

    public PetNotesFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentShareViewModel fragmentShareViewModel = new ViewModelProvider(requireActivity()).get(FragmentShareViewModel.class);
        fragmentShareViewModel.getSelectedPet().observe(getViewLifecycleOwner(), this::getNotes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_notes, container, false);

        firebaseViewModel = new ViewModelProvider(getActivity()).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();


        return mBinding.getRoot();
    }

    private void getNotes(Pet pet) {
        mBinding.progressNotes.setVisibility(View.VISIBLE);
        valueEventListener = databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner= snapshot.getValue(Owner.class);
                retrieveNotes(owner.getAccountId(), pet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    private void retrieveNotes(String accountId, Pet pet) {
       databaseReference.child(Constants.NOTES).child(accountId).child(pet.getId()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               mNoteList.clear();

               for(DataSnapshot data: snapshot.getChildren()) {
                   Note note = data.getValue(Note.class);

                   mNoteList.add(note);
               }

               if(mNoteList.isEmpty()) {
                   mBinding.tvNoNotes.setVisibility(View.VISIBLE);
               } else {
                   setRecyclerView(mNoteList);
                   mBinding.tvNoNotes.setVisibility(View.INVISIBLE);
               }
               mBinding.progressNotes.setVisibility(View.INVISIBLE);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
           }
       });
    }

    private void setRecyclerView(List<Note> mNoteList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerNoteAdapter adapter = new RecyclerNoteAdapter(mNoteList);
        mBinding.recyclerNotes.setLayoutManager(linearLayoutManager);
        mBinding.recyclerNotes.setAdapter(adapter);
        mBinding.recyclerNotes.setHasFixedSize(true);

    }

    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListener);
    }
}