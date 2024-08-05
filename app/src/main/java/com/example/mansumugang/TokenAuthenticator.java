package com.example.mansumugang;

import android.app.ActivityManager;
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
import retrofit2.Retrofit;

/**
 * TokenAuthenticator 클래스는 토큰 갱신을 처리하는 OkHttp Authenticator입니다.
 */
public class TokenAuthenticator implements Authenticator {

    private static final String TAG = "TokenAuthenticator";
    private Context context;
    private boolean isTokenRefreshing = false;

    /**
     * TokenAuthenticator 생성자
     *
     * @param context 애플리케이션 컨텍스트
     */
    public TokenAuthenticator(Context context) {
        this.context = context;
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
        Log.d(TAG, "authenticate() called");

        // 이미 토큰 갱신이 시도되었는지 확인하여 무한 루프 방지
        if (isTokenRefreshing) {
            Log.d(TAG, "Token is already refreshing. Skipping further attempts.");
            return null;
        }

        String accessToken = App.prefs.getToken();       // 현재 액세스 토큰
        String refreshToken = App.prefs.getRefreshToken(); // 리프레시 토큰

        // 인증 실패 코드 확인
        if (response.code() == 401 || response.code() == 403) {
            // 이미 토큰 갱신 시도를 했는지 확인하여 무한 루프 방지
            if (response.request().header("Authorization-refresh") != null &&
                    response.request().header("Authorization-refresh").startsWith("Bearer " + refreshToken)) {
                return null; // 무한 루프 방지를 위해 null 반환
            }

            // 토큰 갱신 시도 중으로 플래그 설정
            isTokenRefreshing = true;

            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            try {
                // 토큰 갱신 요청
                Log.d(TAG,"try block in          " + accessToken + " rt: " + refreshToken);
                Call<RefreshTokenResponse> call = apiService.refreshToken(
                        "Bearer " + accessToken,
                        "Bearer " + refreshToken
                );
                retrofit2.Response<RefreshTokenResponse> tokenResponse = call.execute();
                Log.d(TAG,"newtoken result" + tokenResponse);

                if (tokenResponse.isSuccessful()) {
                    RefreshTokenResponse newToken = tokenResponse.body();

                    if (newToken != null) {
                        // 새로운 액세스 토큰 저장
                        App.prefs.setToken(newToken.getAccessToken());
                        Log.d(TAG, "New access token: " + newToken.getAccessToken());

                        // 새로운 액세스 토큰을 사용하여 원래 요청 재시도
                        return response.request().newBuilder()
                                .header("Authorization", "Bearer " + newToken.getAccessToken())
                                .build();
                    } else {
                        Log.e(TAG, "TokenResponse is null");
                    }
                } else {
                    Log.e(TAG, "토큰 갱신 실패: " + tokenResponse.message());

                    if (tokenResponse.code() == 401) {
                        // NoSuchRefreshTokenError 발생 시 로그아웃 처리
                        String errorBody = tokenResponse.errorBody() != null ? tokenResponse.errorBody().string() : "";
                        if (errorBody.contains("NoSuchRefreshTokenError")) {
                            Log.e(TAG, "Refresh token is invalid. Logging out...");

                            // 위치 서비스가 실행 중인지 확인하고 중지
                            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                            boolean isServiceRunning = false;

//                            수정필요 ++ 리프래쉬 토큰 상실일 경우 모든 토큰을 제거하지만 로케이션이 주기적으로 업데이트 됌, 즉 의도치 않은 로그아웃 시 실행중인 locationservice 중단 불가
                            if (activityManager != null) {
                                for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                                    if (LocationService.class.getName().equals(service.service.getClassName())) {
                                        isServiceRunning = true;
                                        break;
                                    }
                                }
                            }

                            if (isServiceRunning) {
                                Intent stopLocationServiceIntent = new Intent(context, LocationService.class); // LocationService는 위치 업데이트를 제공하는 서비스로 가정
                                context.stopService(stopLocationServiceIntent);
                                Log.d(TAG, "Location service stopped.");
                                // 로그아웃 수행
                                LogoutUtil.performLogout(context);
                            } else {
                                Log.d(TAG, "Location service is not running.");
                                // 로그아웃 수행
                                LogoutUtil.performLogout(context);
                                // 로그인 화면으로 이동
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        }
                    }
                }
            } catch (HttpException e) {
                Log.e(TAG, "HttpException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            } finally {
                isTokenRefreshing = false;
            }
        }

        // 인증 실패 시 null 반환
        return null;
    }
}
