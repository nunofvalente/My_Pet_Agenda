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

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "hour")
    private String hour;

    @ColumnInfo(name = "minutes")
    private String minutes;

    @ColumnInfo(name = "date")
    private String date;

    @Ignore
    private List<String> days;

    @ColumnInfo(name = "days")
    private String roomDays;

    @ColumnInfo(name = "isActive")
    private boolean isActive;

    @Ignore
    public Reminder() {
    }

    public Reminder(int id, String title, String hour, String minutes, String date, String roomDays, boolean isActive) {
        this.id = id;
        this.title = title;
        this.hour = hour;
        this.minutes = minutes;
        this.date = date;
        this.roomDays = roomDays;
        this.isActive = isActive;
    }

    @Ignore
    public Reminder(String title, String hour, String minutes, String date, List<String> days, boolean isActive) {
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

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Exclude
    public String getRoomDays() {
        return roomDays;
    }

    public void setRoomDays(String roomDays) {
        this.roomDays = roomDays;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
