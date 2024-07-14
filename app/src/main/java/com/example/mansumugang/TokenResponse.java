package com.example.mansumugang;

/**
 * TokenResponse 클래스는 토큰 갱신 응답을 나타냅니다.
 */
public class TokenResponse {
    private String accessToken;

    /**
     * 액세스 토큰을 반환합니다.
     * @return 새로운 액세스 토큰
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 액세스 토큰을 설정합니다.
     * @param accessToken 새로운 액세스 토큰
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
