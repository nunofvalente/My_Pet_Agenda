package com.nunovalente.android.mypetagenda.notif;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.nunovalente.android.mypetagenda.R;
import com.nunovalente.android.mypetagenda.activities.RingActivity;
import com.nunovalente.android.mypetagenda.activities.application.App;
import com.nunovalente.android.mypetagenda.util.Constants;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.leapfrog);
        mediaPlayer.setLooping(true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, RingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String reminderTitle = intent.getStringExtra(Constants.TITLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(reminderTitle)
                .setContentText("Hello I'm ringing")
                .setSmallIcon(R.drawable.ic_pets)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        mediaPlayer.start();

        // assigns vibration pattern
        long[] vibratorPattern = {0, 100, 1000};
        vibrator.vibrate(vibratorPattern, 0);

        startForeground(1, notification.build());

        //Recreates the service after system has enough memory
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }
}
