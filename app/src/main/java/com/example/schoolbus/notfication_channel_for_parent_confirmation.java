package com.example.schoolbus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class notfication_channel_for_parent_confirmation extends Application {
    public static final String channel1="cahnnel for parent ";
    @Override
    public void onCreate() {
        super.onCreate();
        createcahnnel();
    }

    private void createcahnnel() {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(channel1,"location confermed", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(2);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setDescription("confirmation");

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);



        }
    }
}
