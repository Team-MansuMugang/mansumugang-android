package com.example.mansumugang;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/**
 * LocationNotificationHelper 클래스는 위치 서비스와 관련된 알림 생성 기능을 제공합니다.
 */
public class LocationNotificationHelper {
    private Context context;

    public LocationNotificationHelper(Context context) {
        this.context = context;
    }

    /**
     * 알림을 생성하기 위한 NotificationCompat.Builder 객체를 반환합니다.
     *
     * @return NotificationCompat.Builder 객체
     */
    public NotificationCompat.Builder getNotificationBuilder() {
        // 알림을 클릭했을 때 실행될 인텐트 생성
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 알림을 빌드하는 NotificationCompat.Builder 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Location Service")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText("Running")
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        // 알림 채널 생성
        createNotificationChannel();
        return builder;
    }

    /**
     * 알림 채널을 생성합니다 (Android 8.0 이상).
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null && notificationManager.getNotificationChannel(Constants.CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        Constants.CHANNEL_ID,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }
}
