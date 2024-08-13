package com.example.mansumugang;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class LocationNotificationHelper {

    private Context context;
    private static final String LOCATION_CHANNEL_ID = Constants.LOCATION_CHANNEL_ID;
    private static final int LOCATION_NOTIFICATION_ID = Constants.LOCATION_NOTIFICATION_ID; // 고유한 알림 ID

    public LocationNotificationHelper(Context context) {
        this.context = context;
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, LOCATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("만수무강 위치 서비스")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText("실행중")
                .setContentIntent(pendingIntent)
                .setAutoCancel(false) // 사용자가 클릭해도 알림이 사라지지 않게 설정
                .setOngoing(true) // 알림을 사용자가 제거할 수 없도록 설정
                .setPriority(NotificationCompat.PRIORITY_LOW); // 낮은 우선순위

        createNotificationChannel();
        return builder;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null && notificationManager.getNotificationChannel(LOCATION_CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        LOCATION_CHANNEL_ID,
                        "Location Service",
                        NotificationManager.IMPORTANCE_LOW
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void showNotification() {
        NotificationCompat.Builder builder = getNotificationBuilder();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(LOCATION_NOTIFICATION_ID, builder.build());
        }
    }

    public void hideNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(LOCATION_NOTIFICATION_ID); // 특정 ID의 알림을 제거
        }
    }
}
