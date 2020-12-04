package com.nunovalente.android.mypetagenda.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nunovalente.android.mypetagenda.data.local.Repository;
import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;

import java.util.List;

public class RoomViewModel extends AndroidViewModel {
    private final Repository repository;
    private final LiveData<List<Pet>> getPets;
    private final LiveData<List<Reminder>> getAllReminders;
    private final LiveData<List<Reminder>> getPetReminders;

    public RoomViewModel(@NonNull Application application, String accountId, String petId) {
        super(application);
        repository = new Repository(application, accountId, petId);
        getPets = repository.getAllPets();
        getAllReminders = repository.getAllReminders(accountId);
        getPetReminders = repository.getPetReminders(petId);
    }

    //-------------------------------Owner Repository ------------------------------------//

    public void insertOwner(Owner owner) {
        repository.insertOwner(owner);
    }

    public void updateOwner(Owner owner) {
        repository.updateOwner(owner);
    }

    public void deleteOwner(Owner owner) {
        repository.deleteOwner(owner);
    }

    public void getOwner(String id) {
        repository.getOwner(id);
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

    public LiveData<List<Pet>> getAllPets() {
        return getPets;
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

    public LiveData<List<Reminder>> getAllReminders() {
        return getAllReminders;
    }

    public LiveData<List<Reminder>> getPetReminders() {
        return getPetReminders;
    }
}
