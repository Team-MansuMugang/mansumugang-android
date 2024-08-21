package com.healthcare.mansumugang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
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
    private LocationHelper locationHelper; // 위치를 가져오는 도우미 객체

    private static final long REFRESH_INTERVAL = 60000L; // 알람 반복 간격, 60초
    private static final int ALARM_REQUEST_CODE = 0; // 알람 요청 코드

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
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
                } else {
                    Log.e(TAG, "Cannot schedule exact alarms, permission not granted.");
                    // 권한을 요청하기 위한 안내 또는 처리
                    Intent exactAlarmIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    context.startActivity(exactAlarmIntent);
                }
            } else {
                alarmManager.setRepeating(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime,
                        REFRESH_INTERVAL,
                        pendingIntent
                );
            }
        }
    }

    public void stopScheduling() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("FETCH_LOCATION_AND_SCHEDULE"); // 동일한 액션 설정
        intent.putExtra("date", ""); // date 인자를 빈 문자열로 설정

        // 알람 설정 시와 동일한 PendingIntent 생성
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        System.out.println("stop scheduling");

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            System.out.println("AlarmManager cancelled successfully");
        } else {
            System.out.println("AlarmManager is null, cannot cancel alarm");
        }
    }


    public void fetchLocationAndSchedule(String date) {
        // 위치 정보를 한 번만 가져옵니다

        locationHelper.fetchLocationOnce();

        new android.os.Handler().postDelayed(() -> {
            double currentLatitude = locationHelper.getLatitude();
            double currentLongitude = locationHelper.getLongitude();

            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                for (int i = 0; i < 4; i++) {
                    String currentDate = getNextDate(date, i);
                    fetchScheduleData(currentDate, currentLatitude, currentLongitude);
                }
            } else {
                Log.e(TAG, "Current location is not available.");
            }
        }, 5000); // 5초 대기
    }

    private void fetchScheduleData(String date, double currentLatitude, double currentLongitude) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        Call<ScheduleResponse> call = apiService.getSchedule("Bearer " + token, date);

        call.enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ScheduleResponse.Schedule> schedules = response.body().getMedicineSchedules();
                    processSchedules(schedules, response.body(), currentLatitude, currentLongitude);
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

    private void processSchedules(List<ScheduleResponse.Schedule> schedules, ScheduleResponse scheduleResponse, double currentLatitude, double currentLongitude) {
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
                Calendar scheduleTime = getScheduleTime(scheduleResponse.getDate() + " " + schedule.getTime());
                // 스케줄된 시간과 비교할 시간을 가져옵니다.

                // 스케줄 시간의 1시간 전과 1시간 후의 시간을 계산합니다.
                Calendar oneHourBefore = (Calendar) scheduleTime.clone();
                oneHourBefore.add(Calendar.HOUR_OF_DAY, -1);

                Calendar oneHourAfter = (Calendar) scheduleTime.clone();
                oneHourAfter.add(Calendar.HOUR_OF_DAY, 1);



                if (currentTime.before(oneHourBefore) && !currentTime.after(oneHourAfter)) {
                    double latitude = schedule.getHospital().getLatitude();
                    double longitude = schedule.getHospital().getLongitude();
                    Log.d(TAG, "Hospital location - Lat: " + latitude + ", Lon: " + longitude);

                    float[] results = new float[1];
                    Location.distanceBetween(currentLatitude, currentLongitude, latitude, longitude, results);
                    float distanceInMeters = results[0];

                    if (distanceInMeters <= 1000) {
                        Log.d(TAG, "병원이 1km 내에 있습니다.");
                        if(!schedule.getHospital().isHospitalStatus()){
                            System.out.println("sendBroadcast");
                            handleTakingButtonClick(schedule.getHospital().getHospitalId(),scheduleResponse.getDate());

                        }
                        // 추가 로직을 여기에 추가 (예: 알림 보내기, 특정 작업 수행 등)
                    } else {
                        Log.d(TAG, "병원이 1km 밖에 있습니다.");
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

    public void handleTakingButtonClick(Long hospitalId, String date) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        IntakeRequest intakeRequest = new IntakeRequest(hospitalId);

        Call<IntakeResponse> call = apiService.inTake("Bearer " + token, intakeRequest);

        call.enqueue(new Callback<IntakeResponse>() {
            @Override
            public void onResponse(Call<IntakeResponse> call, Response<IntakeResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("complete");
                } else {
                    Log.e(TAG, "API 호출 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<IntakeResponse> call, Throwable t) {
                Log.e(TAG, "API 호출 실패", t);
            }
        });
    }

    private Calendar getScheduleTime(String timeStr) {
        Calendar scheduleTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            scheduleTime.setTime(timeFormat.parse(timeStr));
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse schedule time: " + e.getMessage());
            e.printStackTrace();
        }
        return scheduleTime;
    }

    private boolean isPastTime(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String dateTimeString = date + " " + time;
        try {
            Calendar scheduleTime = Calendar.getInstance();
            scheduleTime.setTime(sdf.parse(dateTimeString));

            Calendar currentTime = Calendar.getInstance();
            currentTime.add(Calendar.MINUTE, -1);
            return scheduleTime.before(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
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
