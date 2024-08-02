package com.example.mansumugang;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * BottomNavigationViewHelper 클래스는 BottomNavigationView의 클릭 리스너를 설정하는 역할을 합니다.
 */
public class BottomNavigationViewHelper {

    /**
     * BottomNavigationView의 클릭 리스너를 설정합니다.
     *
     * @param bottomNavigationView 클릭 리스너를 설정할 BottomNavigationView
     * @param activity             현재 Activity
     */
    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView, final Activity activity) {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                int id = item.getItemId();

                // 아이템 선택 상태 설정
                if (id == R.id.recording) {
                    intent = new Intent(activity, RecordActivity.class);

                } else if (id == R.id.schedule) {
                    intent = new Intent(activity, ScheduleActivity.class);

                } else if (id == R.id.guideline) {
                    intent = new Intent(activity, GuidelineActivity.class);

                } else if (id == R.id.settings) {
                    intent = new Intent(activity, SettingsActivity.class);

                }
                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                    activity.finish(); // 현재 Activity 종료
                    return true;
                }
                return false;
            }
        });
//        if (activity instanceof RecordActivity) {
//            bottomNavigationView.setSelectedItemId(R.id.recording);
//        } else if (activity instanceof ScheduleActivity) {
//            bottomNavigationView.setSelectedItemId(R.id.schedule);
//        } else if (activity instanceof GuidelineActivity) {
//            bottomNavigationView.setSelectedItemId(R.id.guideline);
//        } else if (activity instanceof SettingsActivity) {
//            bottomNavigationView.setSelectedItemId(R.id.settings);
//        }
    }

}
