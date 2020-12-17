package com.nunovalente.android.mypetagenda.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.adapters.RecyclerItemClickListener;
import com.nunovalente.android.mypetagenda.adapters.RecyclerNoteAdapter;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.databinding.FragmentPetNotesBinding;
import com.nunovalente.android.mypetagenda.model.Note;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.util.NetworkUtils;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.FragmentShareViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PetNotesFragment extends Fragment implements RecyclerItemClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private FragmentPetNotesBinding mBinding;

    private RoomViewModel roomViewModel;

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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet_notes, container, false);

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(requireActivity()).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();

        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);


        return mBinding.getRoot();
    }

    private void performSyncNotes(Pet pet) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(requireActivity().getString(R.string.app_name), Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString(getString(R.string.pref_account_id), "");
        databaseReference.child(Constants.NOTES).child(accountId).child(pet.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Note note = dataSnapshot.getValue(Note.class);
                    roomViewModel.insertNote(note);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("SYNC", error.getMessage());
            }
        });
    }


    private void getNotes(Pet pet) {
        if (NetworkUtils.checkConnectivity(requireActivity().getApplication()) && FirebaseHelper.getCurrentOwner() != null) {
            loadOnlineNotes(pet);
            performSyncNotes(pet);
        } else {
            loadOfflineNotes(pet);
        }
    }

    private void loadOfflineNotes(Pet pet) {
        mBinding.progressNotes.setVisibility(View.VISIBLE);
        roomViewModel.getAllNotes(pet.getId()).observe(getViewLifecycleOwner(), notes -> {
            if (notes != null) {
                setRecyclerView(notes);
            } else {
                mBinding.progressNotes.setVisibility(View.INVISIBLE);
                mBinding.tvNoNotes.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadOnlineNotes(Pet pet) {
        mBinding.progressNotes.setVisibility(View.VISIBLE);
        valueEventListener = databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                assert owner != null;
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

                for (DataSnapshot data : snapshot.getChildren()) {
                    Note note = data.getValue(Note.class);

                    mNoteList.add(note);
                }

                if (mNoteList.isEmpty()) {
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
        RecyclerNoteAdapter adapter = new RecyclerNoteAdapter(mNoteList, this);
        mBinding.recyclerNotes.setLayoutManager(linearLayoutManager);
        mBinding.recyclerNotes.setAdapter(adapter);
        mBinding.recyclerNotes.setHasFixedSize(true);
        mBinding.progressNotes.setVisibility(View.INVISIBLE);
    }

    private void showDialog(Note note) {
        String accountId = note.getAccountId();
        String petId = note.getPetId();
        String noteId = note.getNoteId();

        AlertDialog.Builder alert = new AlertDialog.Builder(
                requireActivity());
        alert.setTitle(R.string.delete_note);
        alert.setMessage(R.string.are_you_sure_delete_note);
        alert.setPositiveButton(R.string.confirm, (dialog, which) -> {
            DatabaseReference path = databaseReference.child(Constants.NOTES).child(accountId).child(petId).child(noteId);
            path.removeValue();
            roomViewModel.deleteNote(note);
            mNoteList.remove(note);
            if (mBinding.recyclerNotes.getAdapter() != null) {
                mBinding.recyclerNotes.getAdapter().notifyDataSetChanged();
            }
            dialog.dismiss();
        });
        alert.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    @Override
    public void onItemClickListener(int id) {

    }

    @Override
    public void onLongClicked(int id) {
        Note note = mNoteList.get(id);
        showDialog(note);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}