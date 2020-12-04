package com.nunovalente.android.mypetagenda.data.local;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nunovalente.android.mypetagenda.model.Reminder;

@Database(entities = {Reminder.class}, version = 1, exportSchema = false)
public abstract class ReminderDatabase extends RoomDatabase {

    private static final String TAG = ReminderDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "reminder_database";
    private static ReminderDatabase sInstance;
    public abstract ReminderDao reminderDao();


    /**
     * Singleton that creates the Database Instance or returns if already created.
     *
     */
    public static ReminderDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating Database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(), ReminderDatabase.class, DATABASE_NAME).build();
            }
        }
        Log.d(TAG, "Getting database Instance");
        return sInstance;
    }
}
