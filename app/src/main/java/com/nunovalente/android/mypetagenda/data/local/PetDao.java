package com.nunovalente.android.mypetagenda.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nunovalente.android.mypetagenda.model.Pet;

import java.util.List;

@Dao
public interface PetDao {

    @Query("SELECT * FROM pet_database")
    LiveData<List<Pet>> getAllPets();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewPet(Pet pet);

    @Delete
    void deleteNewPet(int id);

    @Query("SELECT * FROM pet_database WHERE id = :id")
    Pet getSelectedPet(int id);
}
