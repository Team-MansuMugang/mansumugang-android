package com.example.mansumugang;

import android.content.Context;
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
        String accessToken = App.prefs.getToken();       // 현재 액세스 토큰
        String refreshToken = App.prefs.getRefreshToken(); // 리프레시 토큰

        if (response.code() == 401 || response.code() == 403) { // 인증 실패 코드 확인
            ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

            try {
                // 토큰 갱신 요청
                Call<TokenResponse> call = apiService.refreshToken(new TokenRequest(accessToken, refreshToken));
                retrofit2.Response<TokenResponse> tokenResponse = call.execute();

                if (tokenResponse.isSuccessful()) {
                    TokenResponse newToken = tokenResponse.body();
                    if (newToken != null) {
                        // 새로운 액세스 토큰 저장
                        App.prefs.setToken(newToken.getAccessToken());

                        // 새로운 액세스 토큰을 사용하여 원래 요청 재시도
                        return response.request().newBuilder()
                                .header("Authorization", "Bearer " + newToken.getAccessToken())
                                .build();
                    }
                } else {
                    Log.e(TAG, "토큰 갱신 실패: " + tokenResponse.message());
                }
            } catch (HttpException e) {
                Log.e(TAG, "HttpException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

        // 인증 실패 시 null 반환
        return null;
    }
}
