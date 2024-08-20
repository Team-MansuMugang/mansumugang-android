package com.healthcare.mansumugang;

import android.content.Context;
import android.widget.Toast;

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
    public static void performLogout(Context context) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        String refreshToken = App.prefs.getRefreshToken(); // 리프레시 토큰

        Call<LogoutResponse> call = apiService.logout("Bearer " + refreshToken);

        // 저장된 토큰 및 사용자 정보 삭제
        App.prefs.setToken(null);
        App.prefs.setRefreshToken(null);
        App.prefs.setUserType(null);

        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
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
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                // 요청 실패
                Toast.makeText(context, "네트워크 오류: 로그아웃 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
