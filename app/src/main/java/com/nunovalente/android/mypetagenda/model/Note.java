package com.nunovalente.android.mypetagenda.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "note_database")
public class Note {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String noteId;

    @ColumnInfo(name = "accountId")
    private String accountId;

    @ColumnInfo(name = "petId")
    private String petId;

    @ColumnInfo(name = "text")
    private String text;

    @Ignore
    public Note() {
    }

    public Note(@NotNull String noteId, String accountId, String petId, String text) {
        this.noteId = noteId;
        this.accountId = accountId;
        this.petId = petId;
        this.text = text;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setId(String accountId) {
        this.accountId = accountId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    @NonNull
    public String getNoteId() {
        return noteId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

}
