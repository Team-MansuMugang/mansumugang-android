package com.healthcare.mansumugang;

/**
 * LoginResponse 클래스는 로그인 응답 시 반환되는 데이터를 정의합니다.
 */
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String userType;

    // Getter 및 Setter 메서드

    /**
     * 액세스 토큰을 반환합니다.
     *
     * @return 액세스 토큰
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 리프레시 토큰을 반환합니다.
     *
     * @return 리프레시 토큰
     */
    public String getRefreshToken() {
        return refreshToken;
    }


    /**
     * 사용자 유형을 반환합니다.
     *
     * @return 사용자 유형
     */
    public String getUserType() {
        return userType;
    }


}
