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
                    return true;
                }
                return false;
            }
        });
    }
}
