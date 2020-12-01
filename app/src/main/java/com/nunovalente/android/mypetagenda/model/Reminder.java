package com.nunovalente.android.mypetagenda.model;

import java.io.Serializable;
import java.util.List;

public class Reminder implements Serializable {

    private String title;
    private String hour;
    private String minutes;
    private String date;
    private List<String> days;
    private boolean isActive;

    public Reminder() {
    }

    public Reminder(String title, String hour, String minutes, String date, List<String> days, boolean isActive) {
        this.title = title;
        this.hour = hour;
        this.minutes = minutes;
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
}
