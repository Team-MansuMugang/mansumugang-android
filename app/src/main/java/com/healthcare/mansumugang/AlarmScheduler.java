package com.healthcare.mansumugang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmScheduler {

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    // 해당 medicineNames에 대해 알람을 취소하는 메서드
    public static void cancelAlarms(Context context, List<List> medicineNames) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (List scheduleItem : medicineNames) {
            if (scheduleItem.size() >= 2) {
                String date = (String) scheduleItem.get(0);
                String time = (String) scheduleItem.get(1);

                String dateTimeString = date + " " + time;
                int notificationId = dateTimeString.hashCode();

                Intent intent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent);
                    Log.d(Constants.ALARM_SCHEDULER_TAG, "Cancelled alarm for: " + dateTimeString);
                }
            }
        }
    }

    public static void scheduleAlarms(Context context, List<List> medicineNames) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (List scheduleItem : medicineNames) {
            if (scheduleItem.size() >= 2) {
                String date = (String) scheduleItem.get(0);
                String time = (String) scheduleItem.get(1);
                String itemFirst = scheduleItem.size() > 2 ? (String) scheduleItem.get(2) : "";
                boolean IsItemHasHospital = itemFirst.contains("hpItem");
                String notificationContent = "";

                if (IsItemHasHospital) {
                    itemFirst = itemFirst.replace("hpItem", "");
                }
                notificationContent +=" "+ itemFirst.trim();

                // 기본 알림 내용 설정

                // 추가 요소가 있는 경우 처리
                for (int i = 3; i < scheduleItem.size(); i++) {
                    String item = scheduleItem.get(i).toString();
                    notificationContent += " " + item.trim();  // trim()으로 공백 제거
                }

                // 알림 내용에 적절한 메시지 추가
                if (IsItemHasHospital) {
                    notificationContent += " 내방 일정이 있습니다.";
                } else {
                    notificationContent += " 복용 일정이 있습니다.";
                }

                // 날짜와 시간 조합 후 알림 스케줄링
                String dateTimeString = date + " " + time;
                int notificationId = dateTimeString.hashCode();

                try {
                    Calendar scheduleTime = Calendar.getInstance();
                    scheduleTime.setTime(dateTimeFormat.parse(dateTimeString));

                    if (scheduleTime.after(Calendar.getInstance())) {
                        scheduleAlarm(context, alarmManager, scheduleTime, notificationContent, notificationId);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(Constants.ALARM_SCHEDULER_TAG, "Invalid schedule item size: " + scheduleItem.size());
            }
        }
    }

    private static void scheduleAlarm(Context context, AlarmManager alarmManager, Calendar scheduleTime, String notificationContent, int notificationId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("notificationContent", notificationContent);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("date", dateTimeFormat.format(scheduleTime.getTime()));
        intent.putExtra("time", scheduleTime.get(Calendar.HOUR_OF_DAY) + ":" + scheduleTime.get(Calendar.MINUTE));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduleTime.getTimeInMillis(), pendingIntent);
            Log.d(Constants.ALARM_SCHEDULER_TAG, "Alarm set for: " + dateTimeFormat.format(scheduleTime.getTime()) + " with content: " + notificationContent + " and ID: " + notificationId);
        } catch (SecurityException e) {
            Log.e(Constants.ALARM_SCHEDULER_TAG, "Exact alarm scheduling failed: " + e.getMessage());
        }
    }

}
