package com.example.crujientepenguins;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {

    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notif = new Notification.Builder(context, "notifyCrujientePenguins")
                .setContentTitle("Time's almost up!")
                .setContentText("Don't forget to bid on these great coupons!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, notif);
    }
}
