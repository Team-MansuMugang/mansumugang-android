package com.example.mansumugang;
import android.Manifest;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ScheduleActivity 클래스는 애플리케이션의 일정 화면을 담당합니다.
 */
public class ScheduleActivity extends AppCompatActivity implements OnDateSelectedListener {
    private RecyclerView weekRecyclerView;
    private static final String TAG = "ScheduleActivity";
    private WeekCalendarAdapter weekCalendarAdapter;
    private LinearLayout layoutBox;
    private ScrollView scrollView;
    private static final int REQUEST_PERMISSIONS = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        checkAndRequestPermissions();
        scrollView = findViewById(R.id.scrollViewBox);
        // RecyclerView 초기화
        weekRecyclerView = findViewById(R.id.weekRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weekRecyclerView.setLayoutManager(layoutManager);

        // 어댑터 설정
        weekCalendarAdapter = new WeekCalendarAdapter(this);
        weekRecyclerView.setAdapter(weekCalendarAdapter);

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.schedule);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        layoutBox = findViewById(R.id.Layoutbox);

        // alarmLocationScheduler 초기화




        // 토큰 확인
        String token = App.prefs.getToken();
        if (token == null || token.isEmpty()) {
            // 토큰이 없으면 로그인 액티비티로 이동
            Intent loginIntent = new Intent(ScheduleActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(0, 0);
            finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
        } else {
            // 토큰이 있으면 알람 패처를 시작
            String todayDate = getTodayDate(); // 현재 날짜를 가져옴
            AlarmLocationScheduler scheduler = new AlarmLocationScheduler(this);
            scheduler.startScheduling(todayDate);

            fetchScheduleData(todayDate);
        }


    }

    /**
     * 권한이 허용되었는지 확인하고 필요한 경우 요청합니다.
     */
    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION); // allow all the time 으로는 사용자가 수정해야함
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한이 거부되었습니다: " + permissions[i], Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getTodayDate() {
        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return today.format(new Date());
    }

    @Override
    public void onDateSelected(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(date);
        Log.d(TAG, "Selected date: " + formattedDate);
        fetchScheduleData(formattedDate);
    }

    /**
     * 위치 서비스가 실행 중인지 확인합니다.
     *
     * @return 위치 서비스가 실행 중이면 true, 그렇지 않으면 false
     */
    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void fetchScheduleData(String date) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        Call<ScheduleResponse> call = apiService.getSchedule("Bearer " + token, date);

        call.enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {

                if (response.isSuccessful()) {
                    displaySchedule(response.body());
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

    public void handleTakingButtonClick(Long hospitalId , String date){
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        IntakeRequest intakeRequest = new IntakeRequest(hospitalId);



        Call call = apiService.inTake("Bearer " + token, intakeRequest );

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                System.out.println(response);

                if (response.isSuccessful()) {
                    // 성공적으로 응답을 받았을 때 처리


                    fetchScheduleData(date);

                } else {
                    // 실패 처리
                    Log.e("ScheduleActivity", "API 호출 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("ScheduleActivity", "API 호출 실패", t);

            }
        });
    }

    public void handleTakingButtonClick(List<Long> medicineIds , String medicineIntakeTime ,String date) {

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        IntakeRequest intakeRequest = new IntakeRequest(medicineIds, medicineIntakeTime, date);
        System.out.println(medicineIds);
        System.out.println(medicineIntakeTime);
        System.out.println(date);
        String token = App.prefs.getToken();

        Call<IntakeResponse> call = apiService.inTake("Bearer " + token, intakeRequest );

        call.enqueue(new Callback<IntakeResponse>() {
            @Override
            public void onResponse(Call<IntakeResponse> call, Response<IntakeResponse> response) {
                System.out.println(response);

                if (response.isSuccessful()) {
                    // 성공적으로 응답을 받았을 때 처리
                    IntakeResponse intakeResponse = response.body();

                    // 성공적일때 fetchdata해서 패치
                    if (intakeResponse != null) {
                        // 응답 처리
                        fetchScheduleData(date);
                    }
                } else {
                    // 실패 처리
                    Log.e("ScheduleActivity", "API 호출 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<IntakeResponse> call, Throwable t) {
                Log.e("ScheduleActivity", "API 호출 실패", t);

            }
        });


    }

    private void displaySchedule(ScheduleResponse scheduleResponse) {

        List<ScheduleResponse.Schedule> schedules = scheduleResponse.getMedicineSchedules();
        layoutBox.removeAllViews();

        if (schedules == null || schedules.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("오늘의 일정이 없습니다.");
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextSize(40);
            layoutBox.addView(emptyView);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) emptyView.getLayoutParams();
            params.topMargin = (int) (300 * getResources().getDisplayMetrics().density); // 16dp
            emptyView.setLayoutParams(params);

            return;
        }

        for (ScheduleResponse.Schedule schedule : schedules) {
            ScheduleItem.createScheduleView(this, layoutBox,  schedule, scheduleResponse.getImageApiUrlPrefix(),scheduleResponse.getDate() ,scrollView);
        }


    }

}
