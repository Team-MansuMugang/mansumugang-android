package com.healthcare.mansumugang;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 인텐트에서 날짜(date), 시간(time) 및 알림 내용(notificationContent)을 가져옵니다.
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String notificationContent = intent.getStringExtra("notificationContent");

        // 알람이 발생했음을 로그로 출력합니다.
        Log.d(Constants.ALARM_RECIVER_TAG, "Alarm triggered. Date: " + date + ", Time: " + time);

        // 위치 및 일정을 가져오는 작업을 실행합니다.
        AlarmLocationScheduler scheduler = new AlarmLocationScheduler(context);
        scheduler.fetchLocationAndSchedule(date);

        // time이 null일 경우 알림을 울리지 않고 메서드를 종료합니다.
        if (date == null || time == null) {
            Log.d(Constants.ALARM_RECIVER_TAG, "Time is null, not triggering notification.");
            return;
        }

        // NotificationManager 객체를 생성합니다.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 알림 채널을 설정합니다 (Android O 이상에서 필요).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // 알림을 구성합니다.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID).setSmallIcon(R.drawable.ic_notification) // 알림 아이콘
                .setContentTitle("Medicine Reminder").setContentText(notificationContent) // 새로운 알림 내용 설정
                .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true); // 알림 클릭 시 자동으로 사라짐

        // 알림을 표시합니다.
        notificationManager.notify(1, builder.build());
    }
}
