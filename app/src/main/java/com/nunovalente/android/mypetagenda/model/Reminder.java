package com.nunovalente.android.mypetagenda.model;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nunovalente.android.mypetagenda.notif.AlarmReceiver;

import java.io.Serializable;
import java.util.Calendar;

import static com.nunovalente.android.mypetagenda.util.Constants.FRIDAY;
import static com.nunovalente.android.mypetagenda.util.Constants.MONDAY;
import static com.nunovalente.android.mypetagenda.util.Constants.PET_REMINDER;
import static com.nunovalente.android.mypetagenda.util.Constants.RECURRING;
import static com.nunovalente.android.mypetagenda.util.Constants.SATURDAY;
import static com.nunovalente.android.mypetagenda.util.Constants.SUNDAY;
import static com.nunovalente.android.mypetagenda.util.Constants.THURSDAY;
import static com.nunovalente.android.mypetagenda.util.Constants.TITLE;
import static com.nunovalente.android.mypetagenda.util.Constants.TUESDAY;
import static com.nunovalente.android.mypetagenda.util.Constants.WEDNESDAY;

@Entity(tableName = "reminder_database")
public class Reminder implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "petId")
    private String petId;

    @ColumnInfo(name = "accountId")
    private String accountId;

    @ColumnInfo(name = "petName")
    private String petName;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "hour")
    private int hour;

    @ColumnInfo(name = "minutes")
    private int minutes;

    private boolean started, recurring;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    @Ignore
    public Reminder() {
    }

    public Reminder(int id, String petId, String accountId, String petName, String title, int hour, int minutes, boolean started, boolean recurring, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        this.id = id;
        this.petId = petId;
        this.accountId = accountId;
        this.petName = petName;
        this.title = title;
        this.hour = hour;
        this.minutes = minutes;
        this.started = started;
        this.recurring = recurring;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public void schedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);
        intent.putExtra(PET_REMINDER, petName);

        intent.putExtra(TITLE, title);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (!recurring) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        } else {
            String stringDays = this.getRecurringDays();
            String[] daysArray = stringDays.split(" ");

            for(String day: daysArray) {
                int dayOfWeek = getDayOfWeek(day);

                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                PendingIntent recurringAlarmPendingIntent = PendingIntent.getBroadcast(context, dayOfWeek + hour, intent, 0);

                final long RUN_WEEKLY = 7 * 24 * 60 * 60 * 1000;
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        RUN_WEEKLY,
                        recurringAlarmPendingIntent
                );
            }
        }

        this.started = true;
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        alarmManager.cancel(pendingIntent);
        this.started = false;

    //    Toast.makeText(context, "Alarm has been canceled!", Toast.LENGTH_SHORT).show();
    }

    public void cancelRecurringAlarm(Context context) {
        String stringDays = this.getRecurringDays();
        String[] daysArray = stringDays.split(" ");

        for (String day : daysArray) {
            int dayOfWeek = getDayOfWeek(day);


            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, dayOfWeek + hour, intent, 0);
            alarmManager.cancel(pendingIntent);
            this.started = false;
        }
    }

    public String getRecurringDays() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (monday) {
            days += "Monday ";
        }
        if (tuesday) {
            days += "Tuesday ";
        }
        if (wednesday) {
            days += "Wednesday ";
        }
        if (thursday) {
            days += "Thursday ";
        }
        if (friday) {
            days += "Friday ";
        }
        if (saturday) {
            days += "Saturday ";
        }
        if (sunday) {
            days += "Sunday ";
        }

        return days;
    }

    private static int getDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek) {
            case "Sunday":
                return Calendar.SUNDAY;
            case "Monday":
                return Calendar.MONDAY;
            case "Tuesday":
                return Calendar.TUESDAY;
            case "Wednesday":
                return Calendar.WEDNESDAY;
            case "Thursday":
                return Calendar.THURSDAY;
            case "Friday":
                return Calendar.FRIDAY;
            case "Saturday":
                return Calendar.SATURDAY;
        }
        return 0;
    }
}

