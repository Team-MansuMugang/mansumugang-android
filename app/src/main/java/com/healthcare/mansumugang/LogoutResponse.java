package com.healthcare.mansumugang;

public class LogoutResponse {
    private String refreshToken;

    // 기본 생성자
    public LogoutResponse() {}

    // 모든 필드를 포함하는 생성자
    public LogoutResponse(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // accessToken에 대한 getter
    public String getAccessToken() {
        return refreshToken;
    }

    // accessToken에 대한 setter
    public void setAccessToken(String accessToken) {
        this.refreshToken = accessToken;
    }
}
