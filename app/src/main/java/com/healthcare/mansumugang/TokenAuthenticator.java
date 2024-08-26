package com.healthcare.mansumugang;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.HttpException;

/**
 * TokenAuthenticator 클래스는 OkHttp의 Authenticator를 구현하여
 * 인증 실패 시 새로운 토큰을 사용하여 요청을 재시도합니다.
 */
public class TokenAuthenticator implements Authenticator {

    private Context context; // 애플리케이션 컨텍스트
    private boolean isTokenRefreshing = false; // 토큰 갱신 여부를 나타내는 플래그
    private AlarmLocationScheduler alarmLocationScheduler; // 알람 위치 스케줄러 객체

    /**
     * TokenAuthenticator의 생성자입니다.
     *
     * @param context 애플리케이션 컨텍스트
     */
    public TokenAuthenticator(Context context) {
        this.context = context;
        this.alarmLocationScheduler = new AlarmLocationScheduler(context);
    }

    /**
     * 인증 실패 시 호출되어 새로운 토큰을 사용하여 요청을 다시 시도합니다.
     *
     * @param route    라우트 정보
     * @param response 인증 실패 응답
     * @return 새로운 인증 토큰을 포함한 요청 또는 null
     * @throws IOException 입출력 예외
     */
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        Log.d(Constants.TOKEN_AUTHENTICATOR, "authenticate() called");

        // 이미 토큰 갱신이 시도 중인지 확인하여 무한 루프 방지
        if (isTokenRefreshing) {
            Log.d(Constants.TOKEN_AUTHENTICATOR, "Token is already refreshing. Skipping further attempts.");
            return null;
        }

        String accessToken = App.prefs.getToken();       // 현재 액세스 토큰
        String refreshToken = App.prefs.getRefreshToken(); // 리프레시 토큰

        // 인증 실패 코드 확인 (401 또는 403)
        if (response.code() == 401 || response.code() == 403) {
            // 이미 토큰 갱신 시도를 했는지 확인하여 무한 루프 방지
            if (response.request().header("Authorization-refresh") != null && response.request().header("Authorization-refresh").startsWith("Bearer " + refreshToken)) {
                return null; // 무한 루프 방지를 위해 null 반환
            }

            // 토큰 갱신 시도 중으로 플래그 설정
            isTokenRefreshing = true;

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            try {
                // 토큰 갱신 요청
                Call<RefreshTokenResponse> call = apiService.refreshToken("Bearer " + accessToken, "Bearer " + refreshToken);
                retrofit2.Response<RefreshTokenResponse> tokenResponse = call.execute();

                if (tokenResponse.isSuccessful()) {
                    // 토큰 갱신 성공
                    RefreshTokenResponse newToken = tokenResponse.body();

                    if (newToken != null) {
                        // 새로운 액세스 토큰 저장
                        App.prefs.setToken(newToken.getAccessToken());
                        Log.d(Constants.TOKEN_AUTHENTICATOR, "New access token: " + newToken.getAccessToken());

                        // 새로운 액세스 토큰을 사용하여 원래 요청 재시도
                        return response.request().newBuilder().header("Authorization", "Bearer " + newToken.getAccessToken()).build();
                    } else {
                        Log.e(Constants.TOKEN_AUTHENTICATOR, "TokenResponse is null");
                    }
                } else {
                    // 토큰 갱신 실패 처리
                    Log.e(Constants.TOKEN_AUTHENTICATOR, "토큰 갱신 실패: " + tokenResponse.message());

                    if (tokenResponse.code() == 401) {
                        // 리프레시 토큰이 유효하지 않은 경우 로그아웃 처리
                        String errorBody = tokenResponse.errorBody() != null ? tokenResponse.errorBody().string() : "";
                        if (errorBody.contains("NoSuchRefreshTokenError")) {
                            Log.e(Constants.TOKEN_AUTHENTICATOR, "Refresh token is invalid. Logging out...");

                            LogoutUtil.performLogout(context, alarmLocationScheduler);
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            } catch (HttpException e) {
                Log.e(Constants.TOKEN_AUTHENTICATOR, "HttpException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(Constants.TOKEN_AUTHENTICATOR, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(Constants.TOKEN_AUTHENTICATOR, "Exception: " + e.getMessage());
            } finally {
                isTokenRefreshing = false; // 토큰 갱신 플래그 리셋
            }
        }

        // 인증 실패 시 null 반환
        return null;
    }
}
