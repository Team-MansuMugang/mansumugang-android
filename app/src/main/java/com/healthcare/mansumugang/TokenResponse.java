package com.healthcare.mansumugang;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    // 기본 생성자
    public TokenResponse() {}

    // 모든 필드를 포함하는 생성자
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // accessToken에 대한 getter
    public String getAccessToken() {
        return accessToken;
    }

    // accessToken에 대한 setter
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // refreshToken에 대한 getter
    public String getRefreshToken() {
        return refreshToken;
    }

    // refreshToken에 대한 setter
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
