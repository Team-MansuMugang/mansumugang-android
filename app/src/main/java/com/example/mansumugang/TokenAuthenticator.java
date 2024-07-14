package com.example.mansumugang;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * TokenAuthenticator 클래스는 만료된 액세스 토큰을 새로 고치는 역할을 합니다.
 */
public class TokenAuthenticator implements Authenticator {

    private static final String TAG = "TokenAuthenticator";
    private Context context;

    /**
     * 생성자
     * @param context 애플리케이션 컨텍스트
     */
    public TokenAuthenticator(Context context) {
        this.context = context;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // 만료된 Access token과 Refresh token을 사용하여 새로운 Access token을 요청합니다.
        String accessToken = App.prefs.getToken();
        String refreshToken = App.prefs.getRefreshToken();

        // 기존 요청이 실패한 경우 응답 코드 확인
        if (response.code() == 401 || response.code() == 403) {
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            // 만료된 Access 토큰과 Refresh 토큰을 사용하여 새로운 Access 토큰을 요청
            Call<TokenResponse> call = apiService.refreshToken(new TokenRequest(accessToken, refreshToken));
            retrofit2.Response<TokenResponse> tokenResponse = call.execute();

            if (tokenResponse.isSuccessful()) {
                TokenResponse newToken = tokenResponse.body();
                if (newToken != null) {
                    // 새로운 Access 토큰을 저장합니다.
                    App.prefs.setToken(newToken.getAccessToken());

                    // 새로운 Access 토큰으로 요청을 다시 시도합니다.
                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + newToken.getAccessToken())
                            .build();
                }
            } else {
                Log.e(TAG, "토큰 갱신 실패: " + tokenResponse.message());
            }
        }

        // 새로운 토큰을 얻지 못하면 null을 반환하여 인증 실패를 처리합니다.
        return null;
    }
}
