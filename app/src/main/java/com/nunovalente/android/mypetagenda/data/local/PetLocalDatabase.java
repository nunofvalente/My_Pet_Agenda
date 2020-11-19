package com.nunovalente.android.mypetagenda.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.nunovalente.android.mypetagenda.model.Pet;

@Database(entities = {Pet.class}, version = 1, exportSchema = false)
public abstract class PetLocalDatabase extends RoomDatabase {

    private static final String TAG = PetLocalDatabase.class.getSimpleName();

}
