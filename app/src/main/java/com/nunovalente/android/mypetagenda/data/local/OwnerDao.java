package com.nunovalente.android.mypetagenda.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nunovalente.android.mypetagenda.model.Owner;

@Dao
public interface OwnerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOwner(Owner owner);

    @Delete
    void deleteOwner(Owner owner);

    @Query("UPDATE owner_database SET name=:nameOwner WHERE id = :id")
    void updateOwner(String nameOwner, String id);

    @Query("SELECT * FROM owner_database WHERE id = :id")
    Owner getOwner(String id);
}
