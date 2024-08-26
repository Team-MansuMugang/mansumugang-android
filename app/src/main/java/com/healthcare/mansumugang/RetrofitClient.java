package com.healthcare.mansumugang;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient 클래스는 Retrofit 인스턴스를 생성하고 관리하는 역할을 합니다.
 */
public class RetrofitClient {
    private static Retrofit retrofit; // Retrofit 인스턴스를 저장하기 위한 변수

    /**
     * Retrofit 인스턴스를 반환합니다.
     * 싱글톤 패턴을 사용하여 이미 생성된 인스턴스가 있으면 해당 인스턴스를 반환하고,
     * 그렇지 않으면 새로 생성하여 반환합니다.
     *
     * @return Retrofit 인스턴스
     */
    public static Retrofit getRetrofitInstance() {
        // retrofit이 null인 경우, 즉 아직 생성되지 않은 경우에만 새로 생성
        if (retrofit == null) {
            // OkHttpClient의 빌더를 사용하여 OkHttpClient 인스턴스를 생성
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    // TokenAuthenticator를 Authenticator로 설정
                    // Authenticator는 인증 실패 시 호출되며 새 액세스 토큰을 얻기 위한 재인증 과정을 수행
                    .authenticator(new TokenAuthenticator(App.getContext()))
                    // OkHttpClient 인스턴스를 빌드하여 생성
                    .build();

            // Retrofit의 빌더를 사용하여 Retrofit 인스턴스를 생성
            retrofit = new Retrofit.Builder()
                    // OkHttpClient를 Retrofit 클라이언트로 설정
                    // 이로 인해 Retrofit이 HTTP 요청 및 응답을 처리할 때 OkHttpClient의 설정과 기능을 사용
                    .client(okHttpClient)
                    // API 호출의 기본 URL을 설정
                    .baseUrl(Constants.BASE_URL)
                    // JSON 데이터를 Java 객체로 변환하고 그 반대로 변환하는 ConverterFactory를 추가
                    .addConverterFactory(GsonConverterFactory.create())
                    // 설정한 옵션을 바탕으로 Retrofit 인스턴스를 빌드하여 생성
                    .build();
        }
        // Retrofit 인스턴스를 반환
        return retrofit;
    }
}
