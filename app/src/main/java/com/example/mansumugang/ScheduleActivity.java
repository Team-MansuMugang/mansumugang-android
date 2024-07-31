package com.example.mansumugang;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * ScheduleActivity 클래스는 애플리케이션의 일정 화면을 담당합니다.
 */
public class ScheduleActivity extends AppCompatActivity {
    private RecyclerView weekRecyclerView;
    private static final String TAG = "ScheduleActivity";
    private WeekCalendarAdapter weekCalendarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // RecyclerView 초기화
        weekRecyclerView = findViewById(R.id.weekRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weekRecyclerView.setLayoutManager(layoutManager);

        // 어댑터 설정
        weekCalendarAdapter = new WeekCalendarAdapter();
        weekRecyclerView.setAdapter(weekCalendarAdapter);

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        // 토큰 확인
        String token = App.prefs.getToken();
        if (token == null || token.isEmpty()) {
            // 토큰이 없으면 로그인 액티비티로 이동
            Intent loginIntent = new Intent(ScheduleActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
        } else {
            // 토큰이 있으면 위치 서비스 시작
            if (isLocationServiceRunning()) {
                stopLocationService();
            }
            startLocationService();
        }
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
}
