package com.healthcare.mansumugang;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * GuidelineActivity 클래스는 앱의 지침 화면을 표시하는 Activity입니다.
 */
public class GuidelineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // GuidelineActivity의 레이아웃을 설정합니다.
        setContentView(R.layout.activity_guideline);

        // BottomNavigationView를 레이아웃에서 찾습니다.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 현재 선택된 항목을 'guideline'으로 설정합니다.
        bottomNavigationView.setSelectedItemId(R.id.guideline);

        // BottomNavigationView에 클릭 리스너를 설정합니다.
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);
    }
}
