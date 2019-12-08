package com.huynhtrongman.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService  {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Mess From FireBase", "From: " + remoteMessage.getFrom());
        showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("message"));
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NotifyChannel_ID = "com.huynhtrongman.fcm.test";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NotifyChannel_ID,"Notification",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Description");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setVibrationPattern(new long[]{0});
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notificationOS = new NotificationCompat.Builder(this,NotifyChannel_ID);
        notificationOS.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        notificationManager.notify(123,notificationOS.build());



    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

}
