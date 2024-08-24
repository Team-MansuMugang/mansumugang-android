package com.healthcare.mansumugang;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * SettingsActivity 클래스는 설정 화면을 담당합니다.
 */
public class SettingsActivity extends AppCompatActivity {
    private AlarmLocationScheduler alarmLocationScheduler; // AlarmLocationScheduler 변수 추가
    private LinearLayout membersContainer; // FamilyMember 정보를 추가할 컨테이너

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        alarmLocationScheduler = new AlarmLocationScheduler(this);

        TextView logoutButton = findViewById(R.id.logout_button);
        membersContainer = findViewById(R.id.members_container); // 레이아웃의 컨테이너 ID

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.settings);

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView, this);

        // 로그아웃 버튼 클릭 리스너 설정
        logoutButton.setOnClickListener(v -> {
            LogoutUtil.performLogout(this, alarmLocationScheduler);
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // 현재 액티비티를 종료하여 백 스택에서 제거
        });

        // API 호출하여 FamilyMember 정보 가져오기
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String token = App.prefs.getToken();
        Call<FamilyMemberResponse> call = apiService.getFamilyMember("Bearer " + token);

        call.enqueue(new Callback<FamilyMemberResponse>() {
            @Override
            public void onResponse(Call<FamilyMemberResponse> call, Response<FamilyMemberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FamilyMemberResponse familyMemberResponse = response.body();
                    addFamilyMemberToView(familyMemberResponse.getSelf(), "본인" , familyMemberResponse.getImageApiUrl());
                    addFamilyMemberToView(familyMemberResponse.getProtector(), "보호자",familyMemberResponse.getImageApiUrl());

                    for (FamilyMemberResponse.FamilyMember otherPatient : familyMemberResponse.getOtherPatients()) {
                        addFamilyMemberToView(otherPatient, "환자",familyMemberResponse.getImageApiUrl());
                    }
                }
            }

            @Override
            public void onFailure(Call<FamilyMemberResponse> call, Throwable t) {
                // 실패 처리
                System.out.println(t);
            }
        });
    }

    private void addFamilyMemberToView(FamilyMemberResponse.FamilyMember member, String role, String getImageApiUrl) {
        if (member == null) return;

        // LinearLayout 생성 (상위 컨테이너)
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dpToPx(15), dpToPx(15), dpToPx(15), 0);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackgroundResource(R.drawable.edit_round);

// ImageView 생성
        System.out.println(getImageApiUrl);
        System.out.println(member.getProfileImageName());
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dpToPx(60), dpToPx(60));
        imageParams.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(imageParams);

        if (member.getProfileImageName() == null) {
            imageView.setImageResource(R.drawable.person);

        } else {

            String imageUrl = getImageApiUrl +member.getProfileImageName();
            Glide.with(this)
                    .load(imageUrl)

                    .into(imageView);
        }

        imageView.setBackgroundResource(R.drawable.edit_round);

// 첫 번째 수평 LinearLayout 생성 (nameTextView와 roleTextView를 위한 레이아웃)
        LinearLayout topLayout = new LinearLayout(this);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

// TextView for name 생성
        TextView nameTextView = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.leftMargin = dpToPx(10);
        nameTextView.setLayoutParams(nameParams);
        nameTextView.setText(member.getName());
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        nameTextView.setTextColor(getResources().getColor(android.R.color.black));
        nameTextView.setTypeface(nameTextView.getTypeface(), android.graphics.Typeface.BOLD);

// TextView for role 생성
        // TextView for role 생성
        TextView roleTextView = new TextView(this);
        LinearLayout.LayoutParams roleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        roleParams.leftMargin = dpToPx(10);
        roleTextView.setLayoutParams(roleParams);
        roleTextView.setText(role);


// 배경 설정 (패딩 이후에 설정)
        if (roleTextView.getText().toString().equals("본인")) {
            roleTextView.setBackgroundResource(R.drawable.week_calender_rounded_corner_active);
            roleTextView.setTextColor(getResources().getColor(android.R.color.white));

        } else {
            roleTextView.setBackgroundResource(R.drawable.edit_round);
        }
        roleTextView.setPadding(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6)); // 패딩 조정
        roleTextView.setGravity(Gravity.CENTER);

// 첫 번째 수평 LinearLayout에 nameTextView와 roleTextView 추가
        topLayout.addView(nameTextView);
        topLayout.addView(roleTextView);

// 두 번째 수평 LinearLayout 생성 (telephoneTextView를 위한 레이아웃)
        LinearLayout bottomLayout = new LinearLayout(this);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

// TextView for telephone 생성
        TextView telephoneTextView = new TextView(this);
        LinearLayout.LayoutParams telephoneParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        telephoneParams.leftMargin = dpToPx(10);
        telephoneTextView.setLayoutParams(telephoneParams);
        telephoneTextView.setText(member.getTelephone() != null ? member.getTelephone() : "전화번호 없음");
        telephoneTextView.setTypeface(nameTextView.getTypeface(), android.graphics.Typeface.BOLD);

        telephoneTextView.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
        telephoneTextView.setGravity(Gravity.CENTER_HORIZONTAL);

// 두 번째 수평 LinearLayout에 telephoneTextView 추가
        bottomLayout.addView(telephoneTextView);

// 수직 LinearLayout 생성 (topLayout과 bottomLayout을 위한 레이아웃)
        LinearLayout verticalLayout = new LinearLayout(this);
        verticalLayout.setOrientation(LinearLayout.VERTICAL);
        verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        verticalLayout.addView(topLayout);
        verticalLayout.addView(bottomLayout);

// 상위 LinearLayout에 ImageView와 verticalLayout 추가
        layout.addView(imageView);
        layout.addView(verticalLayout);

// membersContainer에 최종 layout 추가
        membersContainer.addView(layout);
    }

    // dp 값을 px로 변환하는 메서드
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
