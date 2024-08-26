package com.healthcare.mansumugang;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * BottomNavigationViewHelper 클래스는 BottomNavigationView의 클릭 리스너를 설정하고,
 * 클릭된 메뉴 아이템에 따라 적절한 Activity를 시작하는 역할을 합니다.
 */
public class BottomNavigationViewHelper {

    /**
     * BottomNavigationView의 클릭 리스너를 설정합니다. 각 메뉴 아이템 클릭 시,
     * 지정된 Activity로 전환합니다.
     *
     * @param bottomNavigationView 클릭 리스너를 설정할 BottomNavigationView 객체
     * @param activity             현재 Activity 객체
     */
    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView, final Activity activity) {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                int id = item.getItemId();

                // 선택된 메뉴 아이템의 ID에 따라 적절한 Activity를 설정합니다.
                if (id == R.id.recording) {
                    intent = new Intent(activity, RecordActivity.class);
                } else if (id == R.id.schedule) {
                    intent = new Intent(activity, ScheduleActivity.class);
                } else if (id == R.id.guideline) {
                    intent = new Intent(activity, GuidelineActivity.class);
                } else if (id == R.id.settings) {
                    intent = new Intent(activity, SettingsActivity.class);
                } else if (id == R.id.camera) {
                    intent = new Intent(activity, CameraActivity.class);
                }

                // 유효한 Intent가 설정된 경우, 해당 Activity를 시작하고 현재 Activity를 종료합니다.
                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0); // 전환 애니메이션 없이 변경
                    activity.finish(); // 현재 Activity 종료
                    return true;
                }
                return false;
            }
        });
    }
}
