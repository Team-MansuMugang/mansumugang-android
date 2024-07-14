package com.example.mansumugang;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * ApiService 인터페이스는 API 엔드포인트와 요청 메서드를 정의합니다.
 * 이 인터페이스는 Retrofit 애너테이션을 사용하여 HTTP 요청과 그 속성을 정의합니다.
 */
public interface ApiService {

    /**
     * 서버로 로그인 요청을 보냅니다.
     *
     * @param requestBody 로그인 자격 증명을 포함한 요청 본문
     * @return 서버로부터의 응답을 포함하는 Call 객체
     */
    @Headers("Content-Type: application/json")
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body RequestBody requestBody);
}
