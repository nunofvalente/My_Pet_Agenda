package com.nunovalente.android.mypetagenda.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nunovalente.android.mypetagenda.model.Pet;

import java.util.List;

@Dao
public interface PetDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPet(Pet pet);

    @Update
    void updatePet(Pet pet);

    @Delete
    void deletePet(Pet pet);

    @Query("SELECT * FROM pet_table WHERE id = :id")
    Pet getPet(String id);

    @Query("SELECT * FROM pet_table WHERE accountId = :accountId")
    LiveData<List<Pet>> getAllPets(String accountId);
}
