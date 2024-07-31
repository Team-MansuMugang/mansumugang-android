package com.example.mansumugang;

/**
 * TokenRequest 클래스는 토큰 갱신 요청에 사용됩니다.
 */
public class TokenRequest {
    private String accessToken;
    private String refreshToken;

    public TokenRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
