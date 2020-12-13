package com.nunovalente.android.mypetagenda.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nunovalente.android.mypetagenda.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReminder(Reminder reminder);

    @Update
    void updateReminder(Reminder reminder);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("SELECT * FROM reminder_database WHERE accountId = :accountId ORDER BY id ASC")
    LiveData<List<Reminder>> getAllReminders(String accountId);

    @Query("SELECT * FROM reminder_database WHERE petId = :petId ORDER BY id ASC")
    LiveData<List<Reminder>> getPetReminders(String petId);

    @Query("SELECT * FROM reminder_database WHERE id = :id")
    Reminder getReminder(int id);

}
