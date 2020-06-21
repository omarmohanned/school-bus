package com.example.schoolbus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

class Nofication_reciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        Intent repeating_intent = new Intent(context, parent_info.class);

        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setAutoCancel(true).setContentIntent(pendingIntent).setContentTitle("Reminder")
                .setContentText("please check the place your children will drop off");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(100, builder.build());
    }
}
