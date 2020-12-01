package com.nunovalente.android.mypetagenda.notif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {

    public void createNotificationChannel(Context context, int importance, boolean showBadge, String name, String description) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "${context.packageName}-$name";
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(showBadge);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
