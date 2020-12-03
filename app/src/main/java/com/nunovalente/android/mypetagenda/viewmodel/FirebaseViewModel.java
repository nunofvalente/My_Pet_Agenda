package com.nunovalente.android.mypetagenda.viewmodel;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nunovalente.android.mypetagenda.data.repository.FirebaseRepository;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;

public class FirebaseViewModel extends AndroidViewModel {

    private final FirebaseRepository repository;

    public FirebaseViewModel(@NonNull Application application) {
        super(application);
        repository = FirebaseRepository.getRepository();
    }

    public void registerOwner(Owner owner, Context context, View view) {
        repository.registerOwner(owner, context, view);
    }

    public void signInOwner(String email, String password, Context context, View view) {
        repository.signInOwner(email, password, context, view);
    }

    public void resetPassword(Context context, String email, Dialog dialog) {
        repository.resetPassword(context, email, dialog);
    }

    public void storeImage(Context context, String userId, String storagePath, String fileName, byte[] imageData) {
        repository.storeImage(context, userId, storagePath, fileName, imageData);
    }

    public void storePetImage(Context context, String userId, String storagePath, String fileName, String imageUrl, Pet pet) {
        repository.storePetImage(context, userId, storagePath, fileName, imageUrl, pet);
    }

    public void updatePetInfo(Context context, String userId, String storagePath, String fileName, String imageUrl, Pet pet) {
        repository.updatePetInfo(context, userId, storagePath, fileName, imageUrl, pet);
    }

    public void saveReminder(Reminder reminder, String accountId, String petId) {
        repository.saveReminder(reminder, accountId, petId);
    }

    public FirebaseAuth getAuth() {
        return repository.getAuthentication();
    }

    public DatabaseReference getDatabase() {
        return repository.getDatabaseReference();
    }
}
