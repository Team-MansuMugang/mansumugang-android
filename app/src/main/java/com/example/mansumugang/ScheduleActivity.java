package com.example.mansumugang;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
    private String hospitalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

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



        // 토큰 확인
        String token = App.prefs.getToken();
        if (token == null || token.isEmpty()) {
            // 토큰이 없으면 로그인 액티비티로 이동
            Intent loginIntent = new Intent(ScheduleActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(0, 0);
            finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
        } else {
            // 토큰이 있으면 위치 서비스 시작
            if (isLocationServiceRunning()) {
                stopLocationService();
            }
            startLocationService();

            // 데이터 가져오기
            fetchScheduleData(getTodayDate());
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

    /**
     * 위치 서비스를 시작합니다.
     */
    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
        startService(intent);
    }

    /**
     * 위치 서비스를 중지합니다.
     */
    private void stopLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
        startService(intent);
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

        List<List> medicineNames = new ArrayList<>();

        for (ScheduleResponse.Schedule schedule : schedules) {
            ScheduleItem.createScheduleView(this, layoutBox,  schedule, scheduleResponse.getImageApiUrlPrefix(),scheduleResponse.getDate() ,scrollView);
            schedule.getTime();
            // 스케줄에 포함된 약물들의 ID를 medicineIds 리스트에 추가

            List<String> medicineNamesIn = new ArrayList<>();

            // 스케줄에 포함된 병원의 ID를 hospitalId 정의
            if (schedule.getHospital() != null) {
                hospitalName = schedule.getHospital().getHospitalName() + " hpItem";
                medicineNamesIn.add(hospitalName);
            }

            medicineNamesIn.add(scheduleResponse.getDate());
            medicineNamesIn.add(schedule.getTime());
            for (ScheduleResponse.Schedule.Medicine medicine : schedule.getMedicines()) {
                medicineNamesIn.add(medicine.getMedicineName());
            }

            medicineNames.add(medicineNamesIn);


        }
        System.out.println(medicineNames );
//        AlarmScheduler.scheduleAlarms(this, medicineNames);
        AlarmScheduler.cancelAllAlarms(this);  // 기존의 알람 모두 제거
        AlarmScheduler.scheduleAlarms(this, medicineNames);  // 새롭게 알람 설정


            // 알람 설정 주기 어떡할지? 계속 받아오기 ??


    }

}
