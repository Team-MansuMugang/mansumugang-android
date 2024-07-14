package com.example.mansumugang;

import okhttp3.OkHttpClient;

/**
 * HttpClient 클래스는 OkHttpClient 인스턴스를 생성하고 관리하는 역할을 합니다.
 */
public class HttpClient {
    // OkHttpClient 인스턴스를 저장하기 위한 변수
    private static OkHttpClient okHttpClient;

    /**
     * OkHttpClient 인스턴스를 반환합니다.
     * 싱글톤 패턴을 사용하여 이미 생성된 인스턴스가 있으면 해당 인스턴스를 반환하고,
     * 그렇지 않으면 새로 생성하여 반환합니다.
     *
     * @return OkHttpClient 인스턴스
     */
    public static OkHttpClient getClient() {
        if (okHttpClient == null) {
            // AuthInterceptor를 추가하여 OkHttpClient를 생성합니다.
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .build();
        }
        return okHttpClient;
    }
}
