package com.nunovalente.android.mypetagenda.data.local;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.nunovalente.android.mypetagenda.model.Note;
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
    private final NoteDao noteDao;
    private final ExecutorHelper executor = ExecutorHelper.getInstance();

    public Repository(Application application) {
        OwnerDatabase ownerDatabase = OwnerDatabase.getInstance(application);
        PetDatabase petDatabase = PetDatabase.getInstance(application);
        ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(application);
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        ownerDao = ownerDatabase.ownerDao();
        petDao = petDatabase.petDao();
        reminderDao = reminderDatabase.reminderDao();
        noteDao = noteDatabase.noteDao();
    }


    //-------------------------------Owner Database ------------------------------------//

    public void insertOwner(Owner owner) {
        executor.getDatabaseExecutor().execute(() -> ownerDao.insertOwner(owner));
        Log.d(DB_TAG, "Owner inserted");
    }

    public void updateOwner(String name, String id) {
        executor.getDatabaseExecutor().execute(() -> ownerDao.updateOwner(name, id));
        Log.d(DB_TAG, "Owner updated");
    }

    public void deleteOwner(Owner owner) {
        executor.getDatabaseExecutor().execute(() -> ownerDao.deleteOwner(owner));
        Log.d(DB_TAG, "Owner deleted");
    }

    public Owner getOwner(String id) {
        return ownerDao.getOwner(id);
    }

    //-------------------------------Pet Database -------------------------------------//

    public void insertPet(Pet pet) {
        executor.getDatabaseExecutor().execute(() -> petDao.insertPet(pet));
        Log.d(DB_TAG, "Pet inserted");
    }

    public void updatePet(Pet pet) {
        executor.getDatabaseExecutor().execute(() -> petDao.updatePet(pet));
        Log.d(DB_TAG, "Pet updated");
    }

    public void deletePet(Pet pet) {
        executor.getDatabaseExecutor().execute(() -> petDao.deletePet(pet));
        Log.d(DB_TAG, "Pet deleted");
    }

    public Pet getPet(String id) {
        return petDao.getPet(id);
    }

    public LiveData<List<Pet>> getAllPets(String accountId) {
        return petDao.getAllPets(accountId);
    }

    //-------------------------------Reminder Database -----------------------------------//

    public void insertReminder(Reminder reminder) {
        executor.getDatabaseExecutor().execute(() -> reminderDao.insertReminder(reminder));
        Log.d(DB_TAG, "Reminder inserted");
    }

    public void updateReminder(Reminder reminder) {
        executor.getDatabaseExecutor().execute(() -> reminderDao.updateReminder(reminder));
        Log.d(DB_TAG, "Reminder updated");
    }

    public void deleteReminder(Reminder reminder) {
        executor.getDatabaseExecutor().execute(() -> reminderDao.deleteReminder(reminder));
        Log.d(DB_TAG, "Reminder deleted");
    }

    public LiveData<List<Reminder>> getPetReminders(String petId) {
        return reminderDao.getPetReminders(petId);
    }

    public LiveData<List<Reminder>> getAllReminders(String accountId) {
        return reminderDao.getAllReminders(accountId);
    }

    public Reminder getReminder(int id) {
        return reminderDao.getReminder(id);
    }

    //-------------------------------Note Database ------------------------------------//

    public void insertNote(Note note) {
        executor.getDatabaseExecutor().execute(() -> noteDao.insertNote(note));
        Log.d(DB_TAG, "Note inserted");
    }

    public void updateNote(Note note) {
        executor.getDatabaseExecutor().execute(() -> noteDao.updateNote(note));
        Log.d(DB_TAG, "Note updated");
    }

    public void deleteOwner(Note note) {
        executor.getDatabaseExecutor().execute(() -> noteDao.deleteNote(note));
        Log.d(DB_TAG, "Note deleted");
    }

    public Note getNote(int id) {
        return noteDao.getNote(id);
    }

    public LiveData<List<Note>> getAllNotes(String petId) {
        return noteDao.getAllNotes(petId);
    }

    public LiveData<List<Note>> getAccountAllNotes(String accountId) {
        return noteDao.getAccountAllNotes(accountId);
    }
}
