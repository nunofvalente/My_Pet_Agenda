package com.nunovalente.android.mypetagenda.data.local;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.nunovalente.android.mypetagenda.model.Owner;
import com.nunovalente.android.mypetagenda.model.Pet;
import com.nunovalente.android.mypetagenda.model.Reminder;
import com.nunovalente.android.mypetagenda.util.ExecutorHelper;

import java.util.List;

public class Repository {

    private static final String DB_TAG = "Database Operation";

    private final OwnerDao ownerDao;
    private final ReminderDao reminderDao;
    private final PetDao petDao;
    private final ExecutorHelper executor = ExecutorHelper.getInstance();
    private final LiveData<List<Pet>> getAllPets;
    private final LiveData<List<Reminder>> getAllReminders;
    private final LiveData<List<Reminder>> getPetReminders;

    public Repository(Application application, String accountId, String petId) {
        OwnerDatabase ownerDatabase = OwnerDatabase.getInstance(application);
        PetDatabase petDatabase = PetDatabase.getInstance(application);
        ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(application);
        ownerDao = ownerDatabase.ownerDao();
        petDao = petDatabase.petDao();
        reminderDao = reminderDatabase.reminderDao();
        getAllPets = petDao.getAllPets(accountId);
        getAllReminders = reminderDao.getAllReminders(accountId);
        getPetReminders = reminderDao.getPetReminders(petId);
    }


    //-------------------------------Owner Database ------------------------------------//

    public void insertOwner(Owner owner) {
        executor.getDatabaseExecutor().execute(() -> {
            ownerDao.insertOwner(owner);
        });
        Log.d(DB_TAG, "Owner inserted");
    }

    public void updateOwner(Owner owner) {
        executor.getDatabaseExecutor().execute(() -> {
            ownerDao.updateOwner(owner);
        });
        Log.d(DB_TAG, "Owner updated");
    }

    public void deleteOwner(Owner owner) {
        executor.getDatabaseExecutor().execute(() -> {
            ownerDao.deleteOwner(owner);
        });
        Log.d(DB_TAG, "Owner deleted");
    }

    public Owner getOwner(String id) {
        return ownerDao.getOwner(id);
    }

    //-------------------------------Pet Database -------------------------------------//

    public void insertPet(Pet pet) {
        executor.getDatabaseExecutor().execute(() -> {
            petDao.insertPet(pet);
        });
        Log.d(DB_TAG, "Pet inserted");
    }

    public void updatePet(Pet pet) {
        executor.getDatabaseExecutor().execute(() -> {
            petDao.updatePet(pet);
        });
        Log.d(DB_TAG, "Pet updated");
    }

    public void deletePet(Pet pet) {
        executor.getDatabaseExecutor().execute(() -> {
            petDao.deletePet(pet);
        });
        Log.d(DB_TAG, "Pet deleted");
    }

    public Pet getPet(String id) {
        return petDao.getPet(id);
    }

    public LiveData<List<Pet>> getAllPets() {
        return getAllPets;
    }

    //-------------------------------Reminder Database -----------------------------------//

    public void insertReminder(Reminder reminder) {
        executor.getDatabaseExecutor().execute(() -> {
            reminderDao.insertReminder(reminder);
        });
        Log.d(DB_TAG, "Reminder inserted");
    }

    public void updateReminder(Reminder reminder) {
        executor.getDatabaseExecutor().execute(() -> {
            reminderDao.updateReminder(reminder);
        });
        Log.d(DB_TAG, "Reminder updated");
    }

    public void deleteReminder(Reminder reminder) {
        executor.getDatabaseExecutor().execute(() -> {
            reminderDao.deleteReminder(reminder);
        });
        Log.d(DB_TAG, "Reminder deleted");
    }

    public LiveData<List<Reminder>> getPetReminders(String petId) {
        return getPetReminders;
    }

    public LiveData<List<Reminder>> getAllReminders(String accountId) {
        return getAllReminders;
    }

    public Reminder getReminder(int id) {
        return reminderDao.getReminder(id);
    }
}
