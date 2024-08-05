package com.example.mansumugang;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Query;

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

    /**
     * 서버로 토큰 갱신 요청을 보냅니다.
     * @param accessToken     현재 액세스 토큰
     * @param refreshToken    리프레시 토큰
     * @return TokenResponse 객체를 포함하는 Call
     */
    @Headers("Content-Type: application/json")
    @POST("api/auth/refreshToken")

    Call<RefreshTokenResponse> refreshToken(
            @Header("Authorization") String accessToken,
            @Header("Authorization-refresh") String refreshToken);

    /**
     * 서버로 위치 정보를 저장 요청을 보냅니다.
     *
     * @param accessToken 인증 토큰
     * @param locationRequest 위치 정보 요청 본문
     * @return 서버로부터의 응답을 포함하는 Call 객체
     */
    @Headers("Content-Type: application/json")
    @POST("api/location/save")
    Call<Void> saveLocation(@Header("Authorization") String accessToken, @Body CustomLocationRequest locationRequest);


    /**
     * 서버로 로그아웃 요청을 보냅니다.
     * @param refreshToken    리프레시 토큰
     * @return LogoutResponse 객체를 포함하는 Call
     */
    @Headers("Content-Type: application/json")
    @POST("api/auth/logout")
    Call<LogoutResponse> logout(
            @Header("Authorization-refresh") String refreshToken);


    /**
     * 서버로 일정 요청을 보냅니다.
     *
     * @param accessToken 인증 토큰
     * @param date 조회 일자
     * @return 서버로부터의 응답을 포함하는 Call 객체
     */
    @GET("api/medicine/patient")
    Call<ScheduleResponse> getSchedule(
            @Header("Authorization") String accessToken,
            @Query("date") String date);




}
