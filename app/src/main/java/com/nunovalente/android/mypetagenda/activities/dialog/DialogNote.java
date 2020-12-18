package com.nunovalente.android.mypetagenda.activities.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseHelper;
import com.nunovalente.android.mypetagenda.model.Note;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.util.Constants;
import com.nunovalente.android.mypetagenda.viewmodel.FirebaseViewModel;
import com.nunovalente.android.mypetagenda.viewmodel.RoomViewModel;

public class DialogNote extends AppCompatDialogFragment implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RoomViewModel roomViewModel;

    private TextView mCancel, mAddNote, mError;
    private EditText mNote;
    private String petId;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_notes, null);

        builder.setView(view)
                .setTitle(R.string.add_note);

        FirebaseViewModel firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        databaseReference = firebaseViewModel.getDatabase();
        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);

        mCancel = view.findViewById(R.id.tv_note_cancel);
        mAddNote = view.findViewById(R.id.tv_add_note);
        mNote = view.findViewById(R.id.edit_dialog_note);
        mError = view.findViewById(R.id.tv_note_error);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        petId = preferences.getString(getString(R.string.selected_pet_id), "");

        setListeners();


        return builder.create();
    }

    private void setListeners() {
        mCancel.setOnClickListener(this);
        mAddNote.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_note_cancel) {
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        } else if (view.getId() == R.id.tv_add_note) {
            addNote();
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        }
    }

    private void addNote() {
        valueEventListener = databaseReference.child(Constants.USERS).child(FirebaseHelper.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Owner owner = snapshot.getValue(Owner.class);
                assert owner != null;
                validateNote(owner.getAccountId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validateNote(String accountId) {
        String noteText = mNote.getText().toString();
        if (!noteText.isEmpty()) {
            DatabaseReference path = databaseReference.child(Constants.NOTES).child(accountId).child(petId).push();
            String key = path.getKey();
            assert key != null;
            Note note = new Note(key, accountId, petId, noteText);
            path.setValue(note);


            roomViewModel.insertNote(note);
        } else {
            mError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}
