package com.nunovalente.android.mypetagenda.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

@Entity(tableName = "owner_database")
public class Owner implements Serializable {

    @PrimaryKey
    @Exclude
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    private String password;

    @ColumnInfo(name = "image")
    private String imagePath;

    @ColumnInfo(name = "accountId")
    private String accountId;

    public Owner(String id, String name, String email, String password, String accountId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Ignore
    public Owner(String name, String email, String password, String accountId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountId = accountId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountId() {return accountId;}

    public void setAccountId(String accountId){this.accountId = accountId;}
}
