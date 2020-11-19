package com.nunovalente.android.mypetagenda.viewmodel;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseRepository;
import com.nunovalente.android.mypetagenda.model.Owner;

public class FirebaseViewModel extends AndroidViewModel {

    private final FirebaseRepository repository;

    public FirebaseViewModel(@NonNull Application application) {
        super(application);
        repository = FirebaseRepository.getRepository();
    }

    public void registerOwner(Owner owner, String accountId, Context context, View view) {
        repository.registerOwner(owner, accountId, context, view);
    }

    public void signInOwner(String email, String password, Context context, View view) {
        repository.signInOwner(email, password, context, view);
    }

    public void resetPassword(Context context, String email, Dialog dialog) {
        repository.resetPassword(context, email, dialog);
    }

    public FirebaseAuth getAuth() {
        return repository.getAuthentication();
    }
}
