package com.example.mansumugang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public static void cancelAllAlarms(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "All alarms cancelled.");
        }
    }

    public static void scheduleAlarms(Context context, List<List> medicineNames) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                for (List scheduleItem : medicineNames) {
                    if (scheduleItem.size() >= 3) {
                        // 첫 번째와 두 번째 요소는 날짜와 시간
                        String date = (String) scheduleItem.get(0);
                        String time = (String) scheduleItem.get(1);
                        String itemFirst = (String) scheduleItem.get(2);
                        boolean IsItemHasHospital = false;

                        if (itemFirst.contains("hpItem")){
                            itemFirst.replace("hpItem", "");
                            IsItemHasHospital = true;
                        }
                        // 기본 알림 내용 설정
                        String notificationContent = "";


                        // 추가 요소가 있는 경우 처리
                        for (int i = 3; i < scheduleItem.size(); i++) {
                            String item = scheduleItem.get(i).toString();


                            notificationContent += " " + item.trim();  // trim()으로 공백 제거
                        }

                        // 알림 내용에 적절한 메시지 추가
                        if (scheduleItem.size() > 3 && IsItemHasHospital) {
                            notificationContent += " 복용 및 내방 일정이 있습니다.";
                        } else if (IsItemHasHospital) {
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
                        Log.e(TAG, "Invalid schedule item size: " + scheduleItem.size());
                    }

                }
            } else {
                // API 31 이상에서 알람 권한 요청
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                Log.d(TAG, "Exact alarm scheduling permission is not granted. Requesting permission.");
            }
        } else {
            for (List scheduleItem : medicineNames) {
                if (scheduleItem.size() >= 3) {
                    String date = (String) scheduleItem.get(0);
                    String time = (String) scheduleItem.get(1);
                    String itemFirst = (String) scheduleItem.get(2);
                    boolean IsItemHasHospital = false;

                    if (itemFirst.contains("hpItem")){
                        itemFirst.replace("hpItem", "");
                        IsItemHasHospital = true;
                    }
                    // 기본 알림 내용 설정
                    String notificationContent = "";


                    // 추가 요소가 있는 경우 처리
                    for (int i = 3; i < scheduleItem.size(); i++) {
                        String item = scheduleItem.get(i).toString();


                        notificationContent += " " + item.trim();  // trim()으로 공백 제거
                    }

                    // 알림 내용에 적절한 메시지 추가
                    if (scheduleItem.size() > 3 && IsItemHasHospital) {
                        notificationContent += " 복용 및 내방 일정이 있습니다.";
                    } else if (IsItemHasHospital) {
                        notificationContent += " 내방 일정이 있습니다.";
                    } else {
                        notificationContent += " 복용 일정이 있습니다.";
                    }


                    // 날짜와 시간을 조합하여 고유한 notificationId 생성
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
                    Log.e(TAG, "Invalid schedule item size: " + scheduleItem.size());
                }
            }
        }
    }

    private static void scheduleAlarm(Context context, AlarmManager alarmManager, Calendar scheduleTime, String notificationContent, int notificationId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("notificationContent", notificationContent);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduleTime.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "Alarm set for: " + dateTimeFormat.format(scheduleTime.getTime()) + " with content: " + notificationContent + " and ID: " + notificationId);
        } catch (SecurityException e) {
            Log.e(TAG, "Exact alarm scheduling failed: " + e.getMessage());
        }
    }
}
