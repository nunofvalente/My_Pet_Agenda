package com.nunovalente.android.mypetagenda.notif;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.data.local.Repository;
import com.nunovalente.android.mypetagenda.model.Reminder;

public class RescheduleAlarmsService extends LifecycleService {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Repository repository = new Repository(getApplication());
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString(getApplicationContext().getString(R.string.account_id), "");

        repository.getAllReminders(accountId).observe(this, reminders -> {
            for(Reminder reminder: reminders) {
                if(reminder.isStarted()) {
                    reminder.schedule(getApplicationContext());
                }
            }
        });

        return START_STICKY;
    }
}
