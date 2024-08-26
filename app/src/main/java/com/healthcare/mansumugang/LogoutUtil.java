package com.healthcare.mansumugang;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.app.ActivityManager;
import android.content.Intent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LogoutUtil 클래스는 로그아웃 기능을 제공하는 유틸리티 클래스입니다.
 * 이 클래스는 사용자가 로그아웃할 때 필요한 다양한 작업을 수행합니다.
 */
public class LogoutUtil {

    /**
     * 로그아웃을 수행하는 메서드
     *
     * @param context                현재 컨텍스트
     * @param alarmLocationScheduler 알람 스케줄러 인스턴스, 알람을 관리하는 역할을 수행
     */
    public static void performLogout(Context context, AlarmLocationScheduler alarmLocationScheduler) {
        // Retrofit을 사용하여 API 서비스 인스턴스를 생성합니다.
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // 저장된 리프레시 토큰을 가져옵니다.
        String refreshToken = App.prefs.getRefreshToken();

        // 저장된 액세스 토큰과 리프레시 토큰을 삭제하여 사용자 정보를 초기화합니다.
        App.prefs.setToken(null);
        App.prefs.setRefreshToken(null);

        // 알람 스케줄러가 제공된 경우, 알람 스케줄링을 중지합니다.
        if (alarmLocationScheduler != null) {
            alarmLocationScheduler.stopScheduling();
        }

        // 현재 위치 서비스가 실행 중인지 확인합니다.
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean isServiceRunning = false;

        if (activityManager != null) {
            // 실행 중인 서비스 목록을 조회하여 위치 서비스가 실행 중인지 확인합니다.
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    isServiceRunning = true;
                    break;
                }
            }
        }

        // 위치 서비스가 실행 중인 경우, 위치 서비스를 중지하는 인텐트를 생성하여 서비스를 중지합니다.
        if (isServiceRunning) {
            Intent stopLocationServiceIntent = new Intent(context, LocationService.class);
            stopLocationServiceIntent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            context.startService(stopLocationServiceIntent);
        }

        // 로그아웃 API 호출
        Call<Void> call = apiService.logout("Bearer " + refreshToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 로그아웃 성공 시, 성공 메시지를 표시합니다.
                    System.out.println("response: " + response);
                    Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 401) {
                    // 토큰이 만료된 경우, 로그에 메시지를 기록합니다.
                    Log.d(Constants.LOGOUT, "Token may be expired. Refreshing token.");
                } else {
                    // API 호출 실패 시, 오류 메시지를 분석하고 표시합니다.
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
                    Toast.makeText(context, "오류 발생: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 네트워크 오류로 로그아웃 실패 시, 오류 메시지를 표시합니다.
                Toast.makeText(context, "네트워크 오류: 로그아웃 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
