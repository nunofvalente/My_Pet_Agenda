package com.nunovalente.android.mypetagenda.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "note_database")
public class Note {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private final String id;

    @ColumnInfo(name = "accountId")
    private String accountId;

    @ColumnInfo(name = "petId")
    private String petId;

    @ColumnInfo(name = "text")
    private String text;

    @Ignore
    public Note() {
        id = "";
    }

    @Ignore
    public Note(@NotNull String id, String text) {
        this.id = id;
        this.text = text;
    }

    public Note(@NonNull String id, String accountId, String petId, String text) {
        this.id = id;
        this.accountId = accountId;
        this.petId = petId;
        this.text = text;
    }

    @Exclude
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

    @Exclude
    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


}
