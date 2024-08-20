package com.healthcare.mansumugang;

public class RefreshTokenResponse {
    private String accessToken;

    // 기본 생성자
    public RefreshTokenResponse() {}

    // 모든 필드를 포함하는 생성자
    public RefreshTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // accessToken에 대한 getter
    public String getAccessToken() {
        return accessToken;
    }

    // accessToken에 대한 setter
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
