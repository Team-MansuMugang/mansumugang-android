package com.example.mansumugang;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

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

        layoutBox = findViewById(R.id.Layoutbox); // 이 부분을 추가합니다.



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
            // 오늘 날짜를 가져오기
            Date today = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(today);

            // 데이터 가져오기
            fetchScheduleData(formattedDate);
        }


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

                System.out.println("진입성공");
                if (response.isSuccessful()) {
                    displaySchedule(response.body().getMedicineSchedules() ,response.body().getImageApiUrlPrefix() );
                    System.out.println("wowowowow" + response.body().getImageApiUrlPrefix());
                    System.out.println("출력 성공 : " + response.body());
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

    private void displaySchedule(List<ScheduleResponse.Schedule> schedules , String getImageApiUrlPrefix) {
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
            View scheduleView = getLayoutInflater().inflate(R.layout.schedule_item, layoutBox, false);
            TextView timeView = scheduleView.findViewById(R.id.timeText);
            timeView.setText(" " + schedule.getTime());
            System.out.println(" qwer"  + schedule.getTime());
            // medicineView를 additionalBox에 추가
            LinearLayout additionalBox = scheduleView.findViewById(R.id.additionalBox);

            for (ScheduleResponse.Schedule.Medicine medicine : schedule.getMedicines()) {

                View medicineView = getLayoutInflater().inflate(R.layout.medicine_item, layoutBox, false);

                TextView medicineNameView = medicineView.findViewById(R.id.medicineName);
                TextView hospitalNameView = medicineView.findViewById(R.id.hospitalName);
                TextView descriptionView = medicineView.findViewById(R.id.description);
                ImageView medicineImage = medicineView.findViewById(R.id.medicineImage);
                Button takingButton = medicineView.findViewById(R.id.takingButton);



                medicineNameView.setText(medicine.getMedicineName());

                hospitalNameView.setText(medicine.getHospitalName());
                descriptionView.setText(medicine.getMedicineDescription());

                String imageUrl = getImageApiUrlPrefix + medicine.getMedicineImageName();
                // getMedicineImageName이 null 이면 기본대체 이미지로
                System.out.println("this gay: " + imageUrl);
                Glide.with(this).load(imageUrl).into(medicineImage);

                takingButton.setText("먹었어요");
                takingButton.setBackgroundColor(getResources().getColor(R.color.SecondaryColorDark));
//                takingButton.setBackground(getResources().getDrawable(R.drawable.edit_border_bottom));



                // medicineView를 additionalBox에 추가
//                LinearLayout additionalBox = new LinearLayout(this);
//                additionalBox.setOrientation(LinearLayout.VERTICAL);
                additionalBox.addView(medicineView);

            }
            layoutBox.addView(scheduleView);

        }
    }
}
