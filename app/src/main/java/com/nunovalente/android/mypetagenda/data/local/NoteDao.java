package com.nunovalente.android.mypetagenda.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nunovalente.android.mypetagenda.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM note_database WHERE petId = :petId")
    LiveData<List<Note>> getAllNotes(String petId);

    @Query("SELECT * FROM note_database WHERE petId = :petId")
    List<Note> getAllNotesValue(String petId);

    @Query("SELECT * FROM note_database WHERE accountId = :accountId")
    LiveData<List<Note>> getAccountAllNotes(String accountId);

    @Query("SELECT * FROM note_database WHERE petId = :id")
    Note getNote(int id);
}
