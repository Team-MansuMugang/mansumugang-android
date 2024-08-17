package com.example.mansumugang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmLocationScheduler {

    private static final String TAG = "AlarmLocationScheduler"; // 로그를 찍을 때 사용할 태그
    private Context context; // 컨텍스트 객체
    public LocationHelper locationHelper; // 위치를 가져오는 도우미 객체

    private static final long REFRESH_INTERVAL = 20000L; // 알람이 반복되는 간격, 20초 (20,000 밀리초)
    private static final int ALARM_REQUEST_CODE = 0; // 알람을 식별하기 위한 요청 코드

    public AlarmLocationScheduler(Context context) {
        this.context = context;
        locationHelper = new LocationHelper(context); // 위치 정보를 가져오는 헬퍼 클래스 초기화
    }

    public void startScheduling(String date) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // AlarmReceiver가 실행될 때마다 fetchLocationAndSchedule을 호출하게 설정
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("FETCH_LOCATION_AND_SCHEDULE"); // Custom action 설정
        intent.putExtra("date", date);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = SystemClock.elapsedRealtime();

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                try {
                    alarmManager.setRepeating(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            triggerTime,
                            REFRESH_INTERVAL,
                            pendingIntent
                    );
                } catch (SecurityException e) {
                    Log.e(TAG, "Permission denied for scheduling exact alarms: " + e.getMessage());
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                alarmManager.setRepeating(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime,
                        REFRESH_INTERVAL,
                        pendingIntent
                );
            } else {
                Log.e(TAG, "Cannot schedule exact alarms, permission not granted.");
            }
        }
    }

    public void stopScheduling() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void fetchScheduleData(String date) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        Call<ScheduleResponse> call = apiService.getSchedule("Bearer " + token, date);

        call.enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ScheduleResponse.Schedule> schedules = response.body().getMedicineSchedules();
                    processSchedules(schedules, response.body());
                } else {
                    Log.e(TAG, "Response unsuccessful or empty: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void processSchedules(List<ScheduleResponse.Schedule> schedules, ScheduleResponse scheduleResponse) {
        List<List> medicineNames = new ArrayList<>();

        for (ScheduleResponse.Schedule schedule : schedules) {
            List<String> medicineNamesIn = new ArrayList<>();

            if (isPastTime(scheduleResponse.getDate(), schedule.getTime())) {
                Log.i(TAG, "Skipping past schedule: " + scheduleResponse.getDate() + " Day " + schedule.getTime());
                continue;
            }

            medicineNamesIn.add(scheduleResponse.getDate());
            medicineNamesIn.add(schedule.getTime());

            if (schedule.getHospital() != null) {
                String hospitalName = schedule.getHospital().getHospitalName() + " hpItem";

                // 현재 시간을 가져옵니다.
                Calendar currentTime = Calendar.getInstance();

                // 스케줄된 시간과 비교할 시간을 가져옵니다.
                Calendar scheduleTime = getScheduleTime(schedule.getTime());

                // 스케줄 시간의 1시간 전과 1시간 후의 시간을 계산합니다.
                Calendar oneHourBefore = (Calendar) scheduleTime.clone();
                oneHourBefore.add(Calendar.HOUR_OF_DAY, -1);

                Calendar oneHourAfter = (Calendar) scheduleTime.clone();
                oneHourAfter.add(Calendar.HOUR_OF_DAY, 1);

                // 디버깅 로그 추가
                Log.d(TAG, "Current time: " + formatCalendarTime(currentTime));
                Log.d(TAG, "One hour before: " + formatCalendarTime(oneHourBefore));
                Log.d(TAG, "One hour after: " + formatCalendarTime(oneHourAfter));
                Log.d(TAG, "Is oneHourAfter before currentTime? " + oneHourAfter.before(currentTime));
                Log.d(TAG, "Is one hour before After currentTime ? " + oneHourBefore.after(currentTime)); // @

                // 현재 시간이 스케줄 시간의 1시간 전후 범위에 있는지 확인합니다.
                if (!currentTime.before(oneHourBefore) && !currentTime.after(oneHourAfter)) {
                    // 스케줄된 병원의 위도와 경도를 가져옵니다.
                    double latitude = schedule.getHospital().getLatitude();
                    double longitude = schedule.getHospital().getLongitude();
                    Log.d(TAG, "Hospital location - Lat: " + latitude + ", Lon: " + longitude);

                    // 현재 위치를 가져옵니다.
                    double currentLatitude = locationHelper.getLatitude();
                    double currentLongitude = locationHelper.getLongitude();

                    if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                        // 현재 위치와 병원 위치 간의 거리 계산
                        float[] results = new float[1];
                        Location.distanceBetween(currentLatitude, currentLongitude, latitude, longitude, results);
                        float distanceInMeters = results[0];

                        // 1km (1000m) 내에 있는지 확인
                        if (distanceInMeters <= 1000) {
                            Log.d(TAG, "병원이 1km 내에 있습니다.");
                            // 추가 로직을 여기에 추가 (예: 알림 보내기, 특정 작업 수행 등)
                        } else {
                            Log.d(TAG, "병원이 1km 밖에 있습니다.");
                        }
                    } else {
                        Log.e(TAG, "현재 위치를 가져올 수 없습니다.");
                    }
                } else {
                    Log.d(TAG, "Current time is not within the 1-hour range of the scheduled time.");
                }

                medicineNamesIn.add(hospitalName);
            }

            for (ScheduleResponse.Schedule.Medicine medicine : schedule.getMedicines()) {
                medicineNamesIn.add(medicine.getMedicineName());
            }

            medicineNames.add(medicineNamesIn);
        }

        AlarmScheduler.cancelAlarms(context, medicineNames);
        AlarmScheduler.scheduleAlarms(context, medicineNames);
    }

    private Calendar getScheduleTime(String timeStr) {
        Calendar scheduleTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            scheduleTime.setTime(timeFormat.parse(timeStr));

        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse schedule time: " + e.getMessage());
            e.printStackTrace();
        }
        return scheduleTime;
    }

    private String formatCalendarTime(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(calendar.getTime());
    }



    private boolean isWithinOneHourRange(Calendar currentTime, Calendar scheduleTime) {
        // 스케줄 시간의 정확한 시각 설정
        Calendar oneHourBefore = (Calendar) scheduleTime.clone();
        oneHourBefore.add(Calendar.HOUR_OF_DAY, -1);
        System.out.println(oneHourBefore);

        Calendar oneHourAfter = (Calendar) scheduleTime.clone();
        oneHourAfter.add(Calendar.HOUR_OF_DAY, 1);
        System.out.println(oneHourAfter);
        // 현재 시간과 비교할 시간을 시각적으로 같게 하기 위해 시간, 분, 초 설정
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        oneHourBefore.set(Calendar.SECOND, 0);
        oneHourBefore.set(Calendar.MILLISECOND, 0);
        oneHourAfter.set(Calendar.SECOND, 0);
        oneHourAfter.set(Calendar.MILLISECOND, 0);

        // 현재 시간을 한 시간 전후 범위와 비교
        boolean withinRange = !currentTime.before(oneHourBefore) && !currentTime.after(oneHourAfter);

        Log.d(TAG, "Within one hour range: " + withinRange);
        return withinRange;
    }



    private float calculateDistance(double currentLatitude, double currentLongitude, double latitude, double longitude) {
        float[] results = new float[1];
        Location.distanceBetween(currentLatitude, currentLongitude, latitude, longitude, results);
        return results[0];
    }


    private boolean isPastTime(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String dateTimeString = date + " " + time;
        try {
            Calendar scheduleTime = Calendar.getInstance();
            scheduleTime.setTime(sdf.parse(dateTimeString));

            Calendar currentTime = Calendar.getInstance();
            currentTime.add(Calendar.MINUTE, -1); // 현재 시간에서 1분을 뺌

            return scheduleTime.before(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    public void fetchLocationAndSchedule(String date) {
        locationHelper.fetchLocationOnce();

        for (int i = 0; i < 4; i++) {
            String currentDate = getNextDate(date, i);
            fetchScheduleData(currentDate);
        }
    }

    public String getNextDate(String startDate, int offset) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(sdf.parse(startDate));
            calendar.add(Calendar.DAY_OF_YEAR, offset);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdf.format(calendar.getTime());
    }
}
