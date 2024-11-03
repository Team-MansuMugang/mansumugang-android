package com.healthcare.mansumugang;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ScheduleActivity 클래스는 애플리케이션의 일정 화면을 담당합니다.
 */
public class ScheduleActivity extends AppCompatActivity implements OnDateSelectedListener {
    private RecyclerView weekRecyclerView; // 주간 캘린더를 표시할 RecyclerView
    private WeekCalendarAdapter weekCalendarAdapter; // RecyclerView에 사용할 어댑터
    private LinearLayout layoutBox; // 일정 항목을 추가할 레이아웃 박스
    private ScrollView scrollView; // 일정 항목을 스크롤할 수 있는 ScrollView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule); // 레이아웃 설정

        // ScrollView 초기화
        scrollView = findViewById(R.id.scrollViewBox);

        // RecyclerView 초기화 및 설정
        weekRecyclerView = findViewById(R.id.weekRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weekRecyclerView.setLayoutManager(layoutManager);
        weekCalendarAdapter = new WeekCalendarAdapter(this);
        weekRecyclerView.setAdapter(weekCalendarAdapter);

        // BottomNavigationView 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.schedule_bottom_nav);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        // 일정 항목을 추가할 레이아웃 박스 초기화
        layoutBox = findViewById(R.id.Layoutbox);

        // 사용자 토큰 확인
        String token = App.prefs.getToken();
        if (token == null || token.isEmpty()) {
            // 토큰이 없으면 로그인 액티비티로 이동
            Intent loginIntent = new Intent(ScheduleActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(0, 0); // 전환 애니메이션 없음
            finish(); // 현재 액티비티 종료
        } else {
            // 토큰이 있는 경우, 알람 위치 스케줄러 시작
            String todayDate = getTodayDate(); // 현재 날짜를 가져옴
            AlarmLocationScheduler scheduler = new AlarmLocationScheduler(this);
            scheduler.startScheduling(todayDate); // 알람 스케줄링 시작
            Intent serviceIntent = new Intent(this, LocationService.class);
            serviceIntent.setAction(Constants.ACTION_START_LOCATION_SERVICE);

            // Android O 이상에서는 포그라운드 서비스 시작
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            // 일정 데이터 가져오기
            fetchScheduleData(todayDate);
        }
    }

    /**
     * 현재 날짜를 "yyyy-MM-dd" 형식으로 반환합니다.
     *
     * @return 현재 날짜 문자열
     */
    private String getTodayDate() {
        SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return today.format(new Date());
    }

    /**
     * 날짜 선택 리스너를 구현합니다.
     * 선택된 날짜의 일정 데이터를 가져옵니다.
     *
     * @param date 선택된 날짜
     */
    @Override
    public void onDateSelected(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(date);
        Log.d(Constants.SCHEDULE_ACTIVITY, "Selected date: " + formattedDate);
        fetchScheduleData(formattedDate);
    }

    /**
     * 주어진 날짜의 일정 데이터를 API를 통해 가져옵니다.
     *
     * @param date 일정 데이터를 가져올 날짜
     */
    private void fetchScheduleData(String date) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        Call<ScheduleResponse> call = apiService.getSchedule("Bearer " + token, date);

        call.enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                if (response.isSuccessful()) {
                    displaySchedule(response.body()); // 성공적으로 데이터를 가져왔을 때 일정 표시
                } else if (response.code() == 401) {
                    Log.d(Constants.SCHEDULE_ACTIVITY, "Token may be expired. Refreshing token.");
                } else {
                    // API 호출 실패 시 에러 메시지 처리
                    String errorMessage = "API 호출 실패";
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(errorBody).getAsJsonObject();
                            errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "서버 오류";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(ScheduleActivity.this, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                Log.e(Constants.SCHEDULE_ACTIVITY, "API call failed: " + t.getMessage());
            }
        });
    }

    /**
     * 약물 섭취 버튼 클릭 처리 메소드.
     * 단일 병원 ID를 사용하여 섭취 요청을 보냅니다.
     *
     * @param hospitalId 병원 ID
     * @param date       섭취 날짜
     */
    public void handleTakingButtonClick(Long hospitalId, String date) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        IntakeRequest intakeRequest = new IntakeRequest(hospitalId);

        Call call = apiService.inTake("Bearer " + token, intakeRequest);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                System.out.println(response);

                if (response.isSuccessful()) {
                    // 성공적으로 응답을 받았을 때 일정 데이터 다시 가져오기
                    fetchScheduleData(date);
                } else if (response.code() == 401) {
                    Log.d(Constants.SCHEDULE_ACTIVITY, "Token may be expired. Refreshing token.");
                } else {
                    // 실패 처리
                    String errorMessage = "API 호출 실패";
                    try {
                        String errorBody = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(errorBody).getAsJsonObject();
                        errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "서버 오류";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ScheduleActivity.this, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("ScheduleActivity", "API 호출 실패", t);
            }
        });
    }

    /**
     * 약물 섭취 버튼 클릭 처리 메소드.
     * 다수의 약물 ID와 섭취 시간을 사용하여 섭취 요청을 보냅니다.
     *
     * @param medicineIds        약물 ID 목록
     * @param medicineIntakeTime 섭취 시간
     * @param date               섭취 날짜
     */
    public void handleTakingButtonClick(List<Long> medicineIds, String medicineIntakeTime, String date) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        IntakeRequest intakeRequest = new IntakeRequest(medicineIds, medicineIntakeTime, date);
        System.out.println(medicineIds);
        System.out.println(medicineIntakeTime);
        System.out.println(date);
        String token = App.prefs.getToken();

        Call<Void> call = apiService.inTake("Bearer " + token, intakeRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println(response);

                if (response.isSuccessful()) {
                    // 성공적으로 응답을 받았을 때 일정 데이터 다시 가져오기
                    fetchScheduleData(date);
                } else if (response.code() == 401) {
                    Log.d(Constants.SCHEDULE_ACTIVITY, "Token may be expired. Refreshing token.");
                } else {
                    // 실패 처리
                    String errorMessage = "API 호출 실패";
                    try {
                        String errorBody = response.errorBody().string();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = parser.parse(errorBody).getAsJsonObject();
                        errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "서버 오류";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ScheduleActivity.this, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ScheduleActivity", "API 호출 실패", t);
            }
        });
    }

    /**
     * 일정 응답 데이터를 화면에 표시합니다.
     *
     * @param scheduleResponse 일정 응답 데이터
     */
    private void displaySchedule(ScheduleResponse scheduleResponse) {
        List<ScheduleResponse.Schedule> schedules = scheduleResponse.getMedicineSchedules();
        layoutBox.removeAllViews(); // 기존 뷰 제거

        if (schedules == null || schedules.isEmpty()) {
            showEmptyScheduleMessage(); // 일정이 없을 경우 메시지 표시
            return;
        }

        int firstUpcomingEventIndex = -1;

        // 일정 항목을 레이아웃 박스에 추가
        for (int i = 0; i < schedules.size(); i++) {
            ScheduleResponse.Schedule schedule = schedules.get(i);

            // 일정 항목을 생성하고 추가
            ScheduleItem.createScheduleView(this, layoutBox, schedule, scheduleResponse.getImageApiUrlPrefix(), scheduleResponse.getDate());

            // 첫 번째 다가오는 일정 인덱스 결정
            if (firstUpcomingEventIndex == -1 && !isPastTime(scheduleResponse.getDate(), schedule.getTime())) {
                firstUpcomingEventIndex = i;
            }
        }

        // 첫 번째 다가오는 일정으로 스크롤
        if (firstUpcomingEventIndex != -1) {
            scrollToSchedule(firstUpcomingEventIndex);
        }
    }

    /**
     * 일정이 없을 때 사용자에게 메시지를 표시합니다.
     */
    private void showEmptyScheduleMessage() {
        TextView emptyView = new TextView(this);
        emptyView.setText("오늘의 일정이 없습니다.");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(40);
        layoutBox.addView(emptyView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) emptyView.getLayoutParams();
        params.topMargin = (int) (300 * getResources().getDisplayMetrics().density); // 여백 조정
        emptyView.setLayoutParams(params);
    }

    /**
     * 주어진 인덱스의 일정으로 스크롤합니다.
     *
     * @param index 스크롤할 일정의 인덱스
     */
    private void scrollToSchedule(int index) {
        scrollView.post(() -> {
            View targetView = layoutBox.getChildAt(index);
            if (targetView != null) {
                scrollView.scrollTo(0, targetView.getTop()); // 스크롤하여 보기
            }
        });
    }

    /**
     * 주어진 날짜와 시간으로 일정이 지났는지 확인합니다.
     *
     * @param date 일정 날짜
     * @param time 일정 시간
     * @return 일정이 지났다면 true, 그렇지 않으면 false
     */
    private boolean isPastTime(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String dateTimeString = date + " " + time;
        try {
            Date scheduleTime = sdf.parse(dateTimeString);
            Calendar currentTime = Calendar.getInstance();
            currentTime.add(Calendar.MINUTE, -1); // 약간의 여유를 추가
            return scheduleTime != null && scheduleTime.before(currentTime.getTime());
        } catch (ParseException e) {
            Log.e(Constants.SCHEDULE_ACTIVITY, "Date parsing error: ", e);
            return false;
        }
    }
}
