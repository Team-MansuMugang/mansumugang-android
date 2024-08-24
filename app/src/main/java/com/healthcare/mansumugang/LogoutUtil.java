package com.healthcare.mansumugang;

import android.content.Context;
import android.widget.Toast;
import android.app.ActivityManager;
import android.content.Intent;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LogoutUtil 클래스는 로그아웃 기능을 제공하는 유틸리티 클래스입니다.
 */
public class LogoutUtil {

    /**
     * 로그아웃을 수행하는 메서드
     *
     * @param context 현재 컨텍스트
     */
    public static void performLogout(Context context, AlarmLocationScheduler alarmLocationScheduler) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String refreshToken = App.prefs.getRefreshToken(); // 리프레시 토큰

        Call<Void> call = apiService.logout("Bearer " + refreshToken);

        // 저장된 토큰 및 사용자 정보 삭제
        App.prefs.setToken(null);
        App.prefs.setRefreshToken(null);
        App.prefs.setUserType(null);


        // 알람 스케줄러 중지
        if (alarmLocationScheduler != null) {
            alarmLocationScheduler.stopScheduling();
        }

        // 위치 서비스가 실행 중인지 확인하고 중지
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean isServiceRunning = false;

        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    isServiceRunning = true;
                    break;
                }
            }
        }

        if (isServiceRunning) {
            Intent stopLocationServiceIntent = new Intent(context, LocationService.class);
            stopLocationServiceIntent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            context.startService(stopLocationServiceIntent);
        }


        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 로그아웃 성공
                    System.out.println("response: " + response);
                    Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                } else {
                    // 로그아웃 실패
                    System.out.println("response: " + response);

                    Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 요청 실패
                Toast.makeText(context, "네트워크 오류: 로그아웃 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
