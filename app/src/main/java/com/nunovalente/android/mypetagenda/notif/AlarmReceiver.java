package com.nunovalente.android.mypetagenda.notif;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.nunovalente.android.mypetagenda.util.Constants;

import java.util.Calendar;

import static com.nunovalente.android.mypetagenda.util.Constants.RECURRING;
import static com.nunovalente.android.mypetagenda.util.Constants.TITLE;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startRescheduleAlarmsService(context);
        } else if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()) || Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
            startCancelAndSetService(context);
        }
        else {
            if (!intent.getBooleanExtra(RECURRING, false)) {
                startAlarmService(context, intent);
            }
             {
                if (alarmIsToday(intent)) {
                    startAlarmService(context, intent);
                }
            }
        }
    }

    private void startCancelAndSetService(Context context) {
        Intent intentService = new Intent(context, AlarmCancelAndSetService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private boolean alarmIsToday(Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch (today) {
            case Calendar.MONDAY:
                return intent.getBooleanExtra(Constants.MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(Constants.TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(Constants.WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(Constants.THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(Constants.FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(Constants.SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(Constants.SUNDAY, false);
        }
        return false;
    }

    private void startAlarmService(Context context, Intent intent) {
            Intent intentService = new Intent(context, AlarmService.class);
            intentService.putExtra(TITLE, intent.getStringExtra(TITLE));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService);
            } else {
                context.startService(intentService);
            }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
