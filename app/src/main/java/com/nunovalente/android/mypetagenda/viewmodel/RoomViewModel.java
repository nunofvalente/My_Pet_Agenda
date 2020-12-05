package com.nunovalente.android.mypetagenda.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nunovalente.android.mypetagenda.data.local.Repository;
import com.nunovalente.android.mypetagenda.model.Note;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;

import java.util.List;

public class RoomViewModel extends AndroidViewModel {
    private final Repository repository;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    //-------------------------------Owner Repository ------------------------------------//

    public void insertOwner(Owner owner) {
        repository.insertOwner(owner);
    }

    public void updateOwner(String name, String id) {
        repository.updateOwner(name, id);
    }

    public void deleteOwner(Owner owner) {
        repository.deleteOwner(owner);
    }

    public Owner getOwner(String id) {
        return repository.getOwner(id);
    }

    //-------------------------------Pet Repository --------------------------------------//

    public void insertPet(Pet pet) {
        repository.insertPet(pet);
    }

    public void updatePet(Pet pet) {
        repository.updatePet(pet);
    }

    public void deletePet(Pet pet) {
        repository.deletePet(pet);
    }

    public void getPet(String id) {
        repository.getPet(id);
    }

    public LiveData<List<Pet>> getAllPets(String accountId) {
        return repository.getAllPets(accountId);
    }

    //-------------------------------Reminder Repository --------------------------------------//

    public void insertReminder(Reminder reminder) {
        repository.insertReminder(reminder);
    }

    public void updateReminder(Reminder reminder) {
        repository.updateReminder(reminder);
    }

    public void deleteReminder(Reminder reminder) {
        repository.deleteReminder(reminder);
    }

    public void getReminder(int id) {
        repository.getReminder(id);
    }

    public LiveData<List<Reminder>> getAllReminders(String accountId) {
        return repository.getAllReminders(accountId);
    }

    public LiveData<List<Reminder>> getPetReminders(String petId) {
        return repository.getPetReminders(petId);
    }

    //-------------------------------Note Repository ------------------------------------//

    public void insertNote(Note note) {
        repository.insertNote(note);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }

    public void deleteNote(Note note) {
        repository.deleteOwner(note);
    }

    public void getNote(int id) {
        repository.getNote(id);
    }

    public LiveData<List<Note>> getAllNotes(String petId) {
        return repository.getAllNotes(petId);
    }

    public LiveData<List<Note>> getAccountAllNotes(String accountId) {
        return repository.getAccountAllNotes(accountId);
    }
}
