package com.healthcare.mansumugang;

/**
 * LoginResponse 클래스는 로그인 요청에 대한 서버 응답 데이터를 정의합니다.
 * 이 클래스는 서버에서 로그인 성공 후 클라이언트로 반환되는 정보를 담고 있습니다.
 */
public class LoginResponse {

    // 액세스 토큰: 사용자가 로그인하여 인증된 상태를 유지하기 위해 서버가 발급하는 토큰
    private String accessToken;

    // 리프레시 토큰: 액세스 토큰이 만료되었을 때, 새로운 액세스 토큰을 발급받기 위해 사용되는 토큰
    private String refreshToken;

    // 사용자 유형: 로그인한 사용자의 유형을 나타냅니다. 예를 들어, 사용자 보호자(USER_PROTECTOR) 또는 일반 사용자(USER_PATIENT) 등
    private String userType;

    /**
     * 액세스 토큰을 반환합니다.
     * 이 메서드는 서버에서 반환한 액세스 토큰을 가져오는 데 사용됩니다.
     *
     * @return 액세스 토큰 문자열
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 리프레시 토큰을 반환합니다.
     * 이 메서드는 서버에서 반환한 리프레시 토큰을 가져오는 데 사용됩니다.
     *
     * @return 리프레시 토큰 문자열
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * 사용자 유형을 반환합니다.
     * 이 메서드는 로그인한 사용자의 유형을 가져오는 데 사용됩니다.
     *
     * @return 사용자 유형 문자열
     */
    public String getUserType() {
        return userType;
    }

}
