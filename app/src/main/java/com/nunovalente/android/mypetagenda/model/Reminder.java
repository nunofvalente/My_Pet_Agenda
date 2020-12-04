package com.nunovalente.android.mypetagenda.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "reminder_database")
public class Reminder implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "petId")
    private String petId;

    @ColumnInfo(name = "accountId")
    private String accountId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "hour")
    private String hour;

    @ColumnInfo(name = "minutes")
    private String minutes;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "days")
    private String days;

    @ColumnInfo(name = "isActive")
    private String isActive;

    @Ignore
    public Reminder() {
    }

    public Reminder(int id, String title, String hour, String minutes, String date, String days, String isActive) {
        this.id = id;
        this.title = title;
        this.hour = hour;
        this.minutes = minutes;
        this.date = date;
        this.days = days;
        this.isActive = isActive;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
