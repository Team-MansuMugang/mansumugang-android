package com.example.mansumugang;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient 클래스는 Retrofit 인스턴스를 생성하고 관리하는 역할을 합니다.
 */
public class RetrofitClient {
    // 서버의 기본 URL
    private static final String BASE_URL = "http://minnnisu.iptime.org";
    // Retrofit 인스턴스를 저장하기 위한 변수
    private static Retrofit retrofit;

    /**
     * Retrofit 인스턴스를 반환합니다.
     * 싱글톤 패턴을 사용하여 이미 생성된 인스턴스가 있으면 해당 인스턴스를 반환하고,
     * 그렇지 않으면 새로 생성하여 반환합니다.
     *
     * @return Retrofit 인스턴스
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .authenticator(new TokenAuthenticator(App.getContext()))  // TokenAuthenticator 추가
                    .addInterceptor(new AuthInterceptor())  // AuthInterceptor 추가
                    .build();

            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
