package com.nunovalente.android.mypetagenda.data.local;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nunovalente.android.mypetagenda.model.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static final String TAG = NoteDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "note_database";
    private static NoteDatabase sInstance;
    public abstract NoteDao noteDao();


    /**
     * Singleton that creates the Database Instance or returns if already created.
     *
     */
    public static NoteDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating Database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, DATABASE_NAME).build();
            }
        }
        Log.d(TAG, "Getting database Instance");
        return sInstance;
    }
}
