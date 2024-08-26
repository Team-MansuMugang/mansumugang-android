package com.healthcare.mansumugang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SettingsActivity 클래스는 설정 화면을 담당합니다.
 */
public class SettingsActivity extends AppCompatActivity {
    private AlarmLocationScheduler alarmLocationScheduler; // 알람 위치 스케줄러 객체
    private LinearLayout membersContainer; // 가족 구성원 정보를 추가할 컨테이너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 알람 위치 스케줄러 초기화
        alarmLocationScheduler = new AlarmLocationScheduler(this);

        // 로그아웃 버튼과 컨테이너를 찾습니다.
        TextView logoutButton = findViewById(R.id.logout_button);
        membersContainer = findViewById(R.id.members_container); // 레이아웃에서 가족 구성원 컨테이너 ID

        // 하단 네비게이션 뷰 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.settings); // 기본 선택 항목 설정

        // 하단 네비게이션 뷰 설정 헬퍼 메소드 호출
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        // 로그아웃 버튼 클릭 리스너 설정
        logoutButton.setOnClickListener(v -> {
            // 로그아웃 처리
            LogoutUtil.performLogout(this, alarmLocationScheduler);
            // 로그인 화면으로 이동
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 새로운 태스크로 시작하고 현재 태스크를 지웁니다.
            startActivity(intent);
            finish(); // 현재 액티비티 종료
        });

        // API를 호출하여 가족 구성원 정보 가져오기
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken(); // 토큰 가져오기
        Call<FamilyMemberResponse> call = apiService.getFamilyMember("Bearer " + token);

        // API 호출 비동기 요청
        call.enqueue(new Callback<FamilyMemberResponse>() {
            @Override
            public void onResponse(Call<FamilyMemberResponse> call, Response<FamilyMemberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적으로 응답을 받았을 때
                    FamilyMemberResponse familyMemberResponse = response.body();
                    // 본인, 보호자 및 다른 환자 정보를 추가
                    addFamilyMemberToView(familyMemberResponse.getSelf(), "본인", familyMemberResponse.getImageApiUrl());
                    addFamilyMemberToView(familyMemberResponse.getProtector(), "보호자", familyMemberResponse.getImageApiUrl());
                    for (FamilyMemberResponse.FamilyMember otherPatient : familyMemberResponse.getOtherPatients()) {
                        addFamilyMemberToView(otherPatient, "환자", familyMemberResponse.getImageApiUrl());
                    }
                } else if (response.code() == 401) {
                    // 토큰이 만료된 경우 로그
                    Log.d(Constants.SETTINGS_ACTIVITY, "Token may be expired. Refreshing token.");
                } else {
                    // 오류 처리
                    String errorMessage = "알 수 없는 오류";
                    if (response.errorBody() != null) {
                        try {
                            // 오류 메시지를 가져와서 로그
                            String errorBody = response.errorBody().string();
                            JsonParser parser = new JsonParser();
                            JsonObject jsonObject = parser.parse(errorBody).getAsJsonObject();
                            errorMessage = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "서버 오류";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    // 오류 메시지를 토스트로 표시
                    Toast.makeText(SettingsActivity.this, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FamilyMemberResponse> call, Throwable t) {
                // 실패 처리
                System.out.println(t);
            }
        });
    }

    /**
     * FamilyMember 정보를 뷰에 추가합니다.
     *
     * @param member         FamilyMember 객체
     * @param role           역할(본인, 보호자, 환자 등)
     * @param getImageApiUrl 이미지 API URL
     */
    private void addFamilyMemberToView(FamilyMemberResponse.FamilyMember member, String role, String getImageApiUrl) {
        if (member == null) return; // member가 null인 경우 리턴

        // 상위 LinearLayout 생성
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dpToPx(15), dpToPx(15), dpToPx(15), 0);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.HORIZONTAL); // 수평 배치
        layout.setBackgroundResource(R.drawable.edit_round); // 배경 설정

        // ImageView 생성
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dpToPx(60), dpToPx(60));
        imageParams.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(imageParams);

        if (member.getProfileImageName() == null) {
            imageView.setImageResource(R.drawable.person); // 기본 이미지 설정
        } else {
            // 이미지 URL을 구성하고 Glide를 사용하여 이미지를 로드합니다.
            String imageUrl = getImageApiUrl + member.getProfileImageName();
            Glide.with(this).load(imageUrl).into(imageView);
        }
        imageView.setBackgroundResource(R.drawable.edit_round); // 배경 설정

        // 첫 번째 수평 LinearLayout 생성 (nameTextView와 roleTextView를 위한 레이아웃)
        LinearLayout topLayout = new LinearLayout(this);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // TextView for name 생성
        TextView nameTextView = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.leftMargin = dpToPx(10);
        nameTextView.setLayoutParams(nameParams);
        nameTextView.setText(member.getName());
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        nameTextView.setTextColor(getResources().getColor(android.R.color.black));
        nameTextView.setTypeface(nameTextView.getTypeface(), android.graphics.Typeface.BOLD); // 텍스트를 굵게

        // TextView for role 생성
        TextView roleTextView = new TextView(this);
        LinearLayout.LayoutParams roleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        roleParams.leftMargin = dpToPx(10);
        roleTextView.setLayoutParams(roleParams);
        roleTextView.setText(role);

        // 역할에 따른 배경 설정
        if (roleTextView.getText().toString().equals("본인")) {
            roleTextView.setBackgroundResource(R.drawable.week_calender_rounded_corner_active);
            roleTextView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            roleTextView.setBackgroundResource(R.drawable.edit_round);
        }
        roleTextView.setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6)); // 패딩 설정
        roleTextView.setGravity(Gravity.CENTER);

        // 첫 번째 수평 LinearLayout에 nameTextView와 roleTextView 추가
        topLayout.addView(nameTextView);
        topLayout.addView(roleTextView);

        // 두 번째 수평 LinearLayout 생성 (telephoneTextView를 위한 레이아웃)
        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // TextView for telephone 생성
        TextView telephoneTextView = new TextView(this);
        LinearLayout.LayoutParams telephoneParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        telephoneParams.leftMargin = dpToPx(10);
        telephoneTextView.setLayoutParams(telephoneParams);
        telephoneTextView.setText(member.getTelephone() != null ? member.getTelephone() : "전화번호 없음");
        telephoneTextView.setTypeface(nameTextView.getTypeface(), android.graphics.Typeface.BOLD); // 텍스트를 굵게
        telephoneTextView.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3)); // 패딩 설정
        telephoneTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        // 두 번째 수평 LinearLayout에 telephoneTextView 추가
        bottomLayout.addView(telephoneTextView);

        // 수직 LinearLayout 생성 (topLayout과 bottomLayout을 위한 레이아웃)
        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        verticalLayout.addView(topLayout);
        verticalLayout.addView(bottomLayout);

        // 상위 LinearLayout에 ImageView와 verticalLayout 추가
        layout.addView(imageView);
        layout.addView(verticalLayout);

        // membersContainer에 최종 layout 추가
        membersContainer.addView(layout);
    }

    /**
     * dp 값을 px로 변환하는 메서드입니다.
     *
     * @param dp 변환할 dp 값
     * @return 변환된 px 값
     */
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
