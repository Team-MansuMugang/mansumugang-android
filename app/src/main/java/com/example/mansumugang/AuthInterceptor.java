package com.example.mansumugang;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AuthInterceptor 클래스는 모든 HTTP 요청에 인증 헤더를 추가하는 역할을 합니다.
 */
public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 초기화된 토큰 변수
        String token = "";

        // App 클래스의 prefs 객체가 null이 아닌 경우 토큰 값을 가져옵니다.
        if (App.prefs != null) {
            token = App.prefs.getToken();
        }

        // 기존 요청에 Authorization 헤더를 추가하여 새 요청을 만듭니다.
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        // 새 요청을 진행하여 응답을 반환합니다.
        return chain.proceed(request);
    }
}
