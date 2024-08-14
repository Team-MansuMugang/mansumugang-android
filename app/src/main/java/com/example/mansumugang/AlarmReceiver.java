package com.example.mansumugang;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String MEDICINE_CHANNEL_ID = "medicine_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationContent = intent.getStringExtra("notificationContent");
        int notificationId = intent.getIntExtra("notificationId", (int) System.currentTimeMillis());

        // Notification Intent 설정
        Intent notificationIntent = new Intent(context, ScheduleActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Notification 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MEDICINE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Medicine Reminder")
                .setContentText(notificationContent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(null);  // 중요도 설정

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8.0 이상에서는 NotificationChannel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    MEDICINE_CHANNEL_ID,
                    "Medicine Reminder Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("This channel is used for medicine reminders");
            notificationManager.createNotificationChannel(channel);
        }

        Log.d("AlarmReceiver", "Displaying notification with ID: " + notificationId);
        notificationManager.notify(notificationId, builder.build());
    }
}
