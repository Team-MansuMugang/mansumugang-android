package com.healthcare.mansumugang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * SettingsActivity 클래스는 설정 화면을 담당합니다.
 */
public class SettingsActivity extends AppCompatActivity {
    private AlarmLocationScheduler alarmLocationScheduler; // AlarmLocationScheduler 변수 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        alarmLocationScheduler = new AlarmLocationScheduler(this);

        TextView logoutButton = findViewById(R.id.logout_button);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.settings);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        // 로그아웃 버튼 클릭 리스너 설정
        logoutButton.setOnClickListener(v -> {
            LogoutUtil.performLogout(this, alarmLocationScheduler);

            // 로그아웃 후 로그인 화면으로 이동
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
        });
    }
}
